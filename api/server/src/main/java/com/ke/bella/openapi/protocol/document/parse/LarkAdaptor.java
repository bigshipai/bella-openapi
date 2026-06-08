package com.ke.bella.openapi.protocol.document.parse;

import com.ke.bella.openapi.common.exception.BizParamCheckException;
import com.ke.bella.openapi.common.exception.OneTokenException;
import com.ke.bella.openapi.config.OpenAiServiceFactory;
import com.lark.oapi.Client;
import com.lark.oapi.service.docx.v1.model.Block;
import com.lark.oapi.service.docx.v1.model.Image;
import com.lark.oapi.service.docx.v1.model.ListDocumentBlockReq;
import com.lark.oapi.service.docx.v1.model.ListDocumentBlockResp;
import com.lark.oapi.service.docx.v1.model.ListDocumentBlockRespBody;
import com.lark.oapi.service.docx.v1.model.TableMergeInfo;
import com.lark.oapi.service.docx.v1.model.TextElement;
//import com.theokanning.openai.service.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ke.bella.openapi.protocol.document.parse.LarkClientUtils.deleteFile;
import static com.ke.bella.openapi.protocol.document.parse.LarkClientUtils.getImageUrl;
import static com.ke.bella.openapi.protocol.document.parse.LarkClientUtils.importTask;
import static com.ke.bella.openapi.protocol.document.parse.LarkClientUtils.queryTaskResult;
import static com.ke.bella.openapi.protocol.document.parse.LarkClientUtils.uploadFile;

@Slf4j
@Component("LarkDocumentParse")
public class LarkAdaptor implements DocParseAdaptor<LarkProperty> {
	@Autowired
	private LarkFileCleanupService cleanupService;

	@Autowired
	private OpenAiServiceFactory openAiServiceFactory;

	@Override
	public DocParseTaskInfo doParse(DocParseRequest request, String url, String channelCode, LarkProperty property) {
		try {
			SourceFile sourceFile = request.getFile();
			String dotFileType = "";
			//TODO
//				FileUtil.getFileExtension(sourceFile.getName());
			String fileType = dotFileType.isEmpty() ? dotFileType : dotFileType.substring(1);
			if (property.getSupportTypes() != null && Arrays.stream(property.getSupportTypes()).noneMatch(support -> support.equals(fileType))) {
				throw new BizParamCheckException("File type must be any one of: " + String.join(",", property.getSupportTypes()));
			}
			File tempFile = File.createTempFile("lark_", sourceFile.getName());
			openAiServiceFactory.create().retrieveFileContentAndSave(sourceFile.getId(), tempFile.getPath());
			Client client = LarkClientProvider.client(property.getClientId(), property.getClientSecret());
			String fileToken = uploadFile(client, sourceFile.getName(), property.getUploadDirToken(), tempFile);
			String ticket = importTask(client, fileToken, property.getCloudDirToken(), sourceFile.getName(), fileType);

			// Register file cleanup task
			cleanupService.addCleanupTask(fileToken, ticket, property);

			return DocParseTaskInfo.builder().taskId(TaskIdUtils.buildTaskId(channelCode, ticket)).build();
		} catch (Exception e) {
			throw OneTokenException.fromException(e);
		}
	}

	@Override
	public DocParseResponse queryResult(String taskId, String url, LarkProperty property) {
		Client client = LarkClientProvider.client(property.getClientId(), property.getClientSecret());
		DocParseResponse response = queryTaskResult(client, taskId);
		if ("success".equals(response.getStatus())) {
			DocParseResult result = getDocParseResult(client, response.getToken());
			response.setResult(result);
		}
		response.setCallback(() -> deleteFile(client, response.getToken(), "docx"));
		return response;
	}

	@Override
	public boolean isCompletion(String taskId, String url, LarkProperty property) {
		Client client = LarkClientProvider.client(property.getClientId(), property.getClientSecret());
		DocParseResponse response = queryTaskResult(client, taskId);
		return "success".equals(response.getStatus()) || "failed".equals(response.getStatus());
	}

	@Override
	public String getDescription() {
		return "Lark document parsing";
	}

	@Override
	public Class<?> getPropertyClass() {
		return LarkProperty.class;
	}

	private static DocParseResult getDocParseResult(Client client, String token) {
		List<Block> blocks = getBlocks(client, token, null);
		blocks = blocks.stream().filter(block -> !emptyTitle(block)).collect(Collectors.toList());
		return convertTo(blocks, client);
	}

	private static boolean emptyTitle(Block block) {
		if (block.getBlockType() < 3 || block.getBlockType() > 11) {
			return false;
		}
		String text = null;
		switch (block.getBlockType()) {
			case 3:
				text = extractElementsText(block.getHeading1().getElements());
				break;
			case 4:
				text = extractElementsText(block.getHeading2().getElements());
				break;
			case 5:
				text = extractElementsText(block.getHeading3().getElements());
				break;
			case 6:
				text = extractElementsText(block.getHeading4().getElements());
				break;
			case 7:
				text = extractElementsText(block.getHeading5().getElements());
				break;
			case 8:
				text = extractElementsText(block.getHeading6().getElements());
				break;
			case 9:
				text = extractElementsText(block.getHeading7().getElements());
				break;
			case 10:
				text = extractElementsText(block.getHeading8().getElements());
				break;
			case 11:
				text = extractElementsText(block.getHeading9().getElements());
				break;
		}
		return StringUtils.isBlank(text);
	}

	private static List<Block> getBlocks(Client client, String token, String pageToken) {
		ListDocumentBlockReq req = ListDocumentBlockReq.newBuilder().documentId(token).pageSize(500).pageToken(pageToken).documentRevisionId(-1).build();
		try {
			ListDocumentBlockResp resp = client.docx().v1().documentBlock().list(req);
			if (resp.getCode() != 0) {
				throw new OneTokenException.ChannelException(502, resp.getMsg());
			}
			ListDocumentBlockRespBody body = resp.getData();
			List<Block> blocks = new ArrayList<>(Arrays.asList(body.getItems()));
			if (body.getHasMore()) {
				blocks.addAll(getBlocks(client, token, body.getPageToken()));
			}
			return blocks;
		} catch (Exception e) {
			throw OneTokenException.fromException(e);
		}
	}

	/**
	 * Convert Lark Block list to DocParseResult format
	 *
	 * @param blocks Block list returned by Lark
	 * @param client LarkClient
	 * @return Converted DocParseResult object
	 */
	private static DocParseResult convertTo(List<Block> blocks, Client client) {
		if (blocks == null || blocks.isEmpty()) {
			return null;
		}

		// Find root node (node with empty parent_id)
		Block rootBlock = blocks.stream().filter(block -> block.getParentId() == null || block.getParentId().isEmpty()).findFirst().orElse(blocks.get(0));

		DocParseResult result = new DocParseResult();

		// Set root node info
		result.setSummary("");
		result.setPath(null); // Root node path is null
		result.setElement(null); // Root node element is null

		// Get all direct child nodes belonging to root
		List<Block> rootChildren = blocks.stream().filter(block -> rootBlock.getBlockId().equals(block.getParentId())).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

		// Build tree structure by heading levels
		result.setChildren(buildHierarchicalStructure(rootChildren, blocks, client));

		return result;
	}

	/**
	 * Build tree structure by heading levels
	 *
	 * @param blocks    Block list to process
	 * @param allBlocks All blocks list (for finding child nodes)
	 * @param client    LarkClient
	 * @return Built DocParseResult list
	 */
	private static List<DocParseResult> buildHierarchicalStructure(List<Block> blocks, List<Block> allBlocks, Client client) {
		List<DocParseResult> results = new ArrayList<>();

		for (int i = 0; i < blocks.size(); i++) {
			Block currentBlock = blocks.get(i);
			int currentLevel = getHeadingLevel(currentBlock);

			// Create current node
			DocParseResult current = new DocParseResult();
			current.setSummary("");
			current.setPath(Arrays.asList(results.size() + 1)); // Path starts from 1
			current.setElement(createElement(currentBlock, allBlocks, client));

			// If heading, find content belonging to this heading
			if (currentLevel > 0) {
				List<Block> childBlocks = new ArrayList<>();

				// Find all content before next same-level or higher-level heading
				for (int j = i + 1; j < blocks.size(); j++) {
					Block nextBlock = blocks.get(j);
					int nextLevel = getHeadingLevel(nextBlock);

					// Stop when encountering same-level or higher-level heading
					if (nextLevel > 0 && nextLevel <= currentLevel) {
						break;
					}

					childBlocks.add(nextBlock);
				}

				// Recursively build child structure
				current.setChildren(buildHierarchicalStructure(childBlocks, allBlocks, client));

				// Update paths
				updateChildrenPaths(current.getChildren(), current.getPath());

				// Skip already processed child nodes
				i += childBlocks.size();
			} else {
				// Non-heading node, find direct child nodes (based on parent_id)
				// Exclude table-related block types as they are already processed in table rows
				List<Block> directChildren = allBlocks.stream().filter(block -> currentBlock.getBlockId().equals(block.getParentId())).filter(block -> !isTableRelatedBlock(block)) // Exclude table-related blocks
					.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

				if (!directChildren.isEmpty()) {
					current.setChildren(buildHierarchicalStructure(directChildren, allBlocks, client));
					updateChildrenPaths(current.getChildren(), current.getPath());
				}
			}

			results.add(current);
		}

		return results;
	}

	/**
	 * Check if block is table-related type
	 *
	 * @param block Block object
	 * @return true if table-related
	 */
	private static boolean isTableRelatedBlock(Block block) {
		if (block == null) return false;
		return 32 == block.getBlockType();
	}

	/**
	 * Check if Block contains complex content (non-plain-text)
	 *
	 * @param block Block object
	 * @return true if complex content
	 */
	private static boolean isComplexBlock(Block block) {
		if (block == null) return false;

		switch (block.getBlockType()) {
			case 2: // text - plain text, not complex
				return false;
			case 27: // image - image is complex
			case 23: // equation - equation is complex
			case 15: // code - code block is complex
			case 31: // table - nested table is complex
			case 12: // bullet - list item is complex
			case 13: // ordered - ordered list item is complex
				return true;
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11: // Various heading levels
				return true; // Headings are also complex in cells
			default:
				return false;
		}
	}

	/**
	 * Get heading level
	 *
	 * @param block Block object
	 * @return Heading level (1-9), 0 for non-heading
	 */
	private static int getHeadingLevel(Block block) {
		if (block == null) return 0;

		switch (block.getBlockType()) {
			case 3:
				return 1; // heading1
			case 4:
				return 2; // heading2
			case 5:
				return 3; // heading3
			case 6:
				return 4; // heading4
			case 7:
				return 5; // heading5
			case 8:
				return 6; // heading6
			case 9:
				return 7; // heading7
			case 10:
				return 8; // heading8
			case 11:
				return 9; // heading9
			default:
				return 0; // Non-heading
		}
	}

	/**
	 * Update child node paths
	 *
	 * @param children   Child node list
	 * @param parentPath Parent node path
	 */
	private static void updateChildrenPaths(List<DocParseResult> children, List<Integer> parentPath) {
		if (children == null || children.isEmpty()) return;

		for (int i = 0; i < children.size(); i++) {
			DocParseResult child = children.get(i);
			List<Integer> childPath = new ArrayList<>();
			if (parentPath != null) {
				childPath.addAll(parentPath);
			}
			childPath.add(i + 1); // Path starts from 1
			child.setPath(childPath);

			// Recursively update grandchild nodes
			updateChildrenPaths(child.getChildren(), childPath);
		}
	}

	/**
	 * Create Element object from Block
	 *
	 * @param block     Lark Block object
	 * @param allBlocks All blocks list (for finding table child nodes)
	 * @param client    LarkClient
	 * @return Element object
	 */
	private static DocParseResult.Element createElement(Block block, List<Block> allBlocks, Client client) {
		DocParseResult.Element element = new DocParseResult.Element();

		// Set type and content based on block_type
		switch (block.getBlockType()) {
			case 1: // page
				element.setType("Text");
				if (block.getPage() != null) {
					element.setText(extractElementsText(block.getPage().getElements()));
				}
				break;
			case 2: // text
				element.setType("Text");
				if (block.getText() != null) {
					element.setText(extractElementsText(block.getText().getElements()));
				}
				break;
			case 3: // heading1
				element.setType("Title");
				if (block.getHeading1() != null) {
					element.setText(extractElementsText(block.getHeading1().getElements()));
				}
				break;
			case 4: // heading2
				element.setType("Title");
				if (block.getHeading2() != null) {
					element.setText(extractElementsText(block.getHeading2().getElements()));
				}
				break;
			case 5: // heading3
				element.setType("Title");
				if (block.getHeading3() != null) {
					element.setText(extractElementsText(block.getHeading3().getElements()));
				}
				break;
			case 6: // heading4
				element.setType("Title");
				if (block.getHeading4() != null) {
					element.setText(extractElementsText(block.getHeading4().getElements()));
				}
				break;
			case 7: // heading5
				element.setType("Title");
				if (block.getHeading5() != null) {
					element.setText(extractElementsText(block.getHeading5().getElements()));
				}
				break;
			case 8: // heading6
				element.setType("Title");
				if (block.getHeading6() != null) {
					element.setText(extractElementsText(block.getHeading6().getElements()));
				}
				break;
			case 9: // heading7
				element.setType("Title");
				if (block.getHeading7() != null) {
					element.setText(extractElementsText(block.getHeading7().getElements()));
				}
				break;
			case 10: // heading8
				element.setType("Title");
				if (block.getHeading8() != null) {
					element.setText(extractElementsText(block.getHeading8().getElements()));
				}
				break;
			case 11: // heading9
				element.setType("Title");
				if (block.getHeading9() != null) {
					element.setText(extractElementsText(block.getHeading9().getElements()));
				}
				break;
			case 12: // bullet
				element.setType("ListItem");
				if (block.getBullet() != null) {
					element.setText(extractElementsText(block.getBullet().getElements()));
				}
				break;
			case 13: // ordered
				element.setType("ListItem");
				if (block.getOrdered() != null) {
					element.setText(extractElementsText(block.getOrdered().getElements()));
				}
				break;
			case 15: // code
				element.setType("Code");
				if (block.getCode() != null) {
					element.setText(extractElementsText(block.getCode().getElements()));
				}
				break;
			case 23: // equation
				element.setType("Formula");
				if (block.getEquation() != null) {
					element.setText(extractElementsText(block.getEquation().getElements()));
				}
				break;
			case 31: // table
				element.setType("Table");
				if (block.getTable() != null) {
					element.setRows(convertTableRows(block, allBlocks, client));
				}
				break;
			case 27: // image
				element.setType("Figure");
				if (block.getImage() != null) {
					DocParseResult.Image image = convertImage(block.getImage(), client);
					element.setImage(image);
				}
				break;
			case 32: // table_cell
				element.setType("Text"); // Table cell treated as text
				// Table cell content handled through children
				break;
			default:
				element.setType("Text");
				element.setText("");
		}

		return element;
	}

	/**
	 * Extract text content from elements array
	 *
	 * @param elements Text element array
	 * @return Extracted text
	 */
	private static String extractElementsText(TextElement[] elements) {
		if (elements == null || elements.length == 0) {
			return "";
		}

		StringBuilder text = new StringBuilder();

		for (TextElement element : elements) {
			if (element != null) {
				String content = extractTextFromElement(element);
				if (!content.isEmpty()) {
					text.append(content);
				}
			}
		}

		return text.toString();
	}

	/**
	 * Extract text content from a single TextElement
	 *
	 * @param element TextElement object
	 * @return Extracted text content
	 */
	private static String extractTextFromElement(TextElement element) {
		if (element == null) {
			return "";
		}

		// Extract text based on different TextElement types
		if (element.getTextRun() != null && element.getTextRun().getContent() != null) {
			// Plain text
			return element.getTextRun().getContent();
		} else if (element.getMentionUser() != null) {
			// @user
			return "@" + (element.getMentionUser().getUserId() != null ? element.getMentionUser().getUserId() : "user");
		} else if (element.getMentionDoc() != null) {
			// @doc
			return "@doc:" + element.getMentionDoc().getTitle();
		} else if (element.getReminder() != null) {
			// Date reminder
			return "[Reminder] " + element.getReminder().getNotifyTime();
		} else if (element.getFile() != null) {
			// Inline attachment
			return "[File] " + element.getMentionDoc().getTitle();
		} else if (element.getEquation() != null) {
			// Equation
			return element.getEquation().getContent();
		}
		return "";
	}

	/**
	 * Convert table row data
	 *
	 * @param block     Table block
	 * @param allBlocks All blocks list
	 * @param client    LarkClient
	 * @return Row data list
	 */
	private static List<DocParseResult.Row> convertTableRows(Block block, List<Block> allBlocks, Client client) {
		List<DocParseResult.Row> rows = new ArrayList<>();

		if (block.getTable() == null || block.getTable().getCells() == null || block.getTable().getProperty() == null) {
			return rows;
		}

		try {
			// Get table properties
			Integer columnSize = block.getTable().getProperty().getColumnSize();
			Integer rowSize = block.getTable().getProperty().getRowSize();
			String[] cellIds = block.getTable().getCells();

			if (columnSize == null || rowSize == null || cellIds == null) {
				return rows;
			}

			int[][] position = new int[rowSize][columnSize];

			// Build cellId to Block mapping
			Map<String, Block> cellBlockMap = allBlocks.stream().filter(b -> b.getTableCell() != null).collect(Collectors.toMap(Block::getBlockId, java.util.function.Function.identity(), (existing, replacement) -> existing));

			// Build table by rows
			for (int row = 0; row < rowSize; row++) {
				DocParseResult.Row rowData = new DocParseResult.Row();
				List<DocParseResult.Cell> cells = new ArrayList<>();
				boolean hasValidCells = false;

				// Build cells by columns
				for (int col = 0; col < columnSize; col++) {

					int cellIndex = row * columnSize + col;
					if (cellIndex < cellIds.length) {
						String cellId = cellIds[cellIndex];
						Block cellBlock = cellBlockMap.get(cellId);

						DocParseResult.Cell cell = new DocParseResult.Cell();

						// Calculate cell merge info
						int rowSpan = 1;
						int colSpan = 1;

						// Get merge info from table's merge_info
						if (block.getTable().getProperty().getMergeInfo() != null && cellIndex < block.getTable().getProperty().getMergeInfo().length) {
							TableMergeInfo mergeInfo = block.getTable().getProperty().getMergeInfo()[cellIndex];
							if (mergeInfo != null) {
								rowSpan = mergeInfo.getRowSpan();
								colSpan = mergeInfo.getColSpan();
							}
						}

						// Record cell coordinate info
						// Calculate cell coordinate range (starting from 1)
						int startRow = row + 1;
						int endRow = row + rowSpan;
						int startCol = col + 1;
						int endCol = col + colSpan;

						List<Integer> cellCoords = Arrays.asList(startRow, endRow, startCol, endCol);
						cell.setPath(cellCoords);

						// Handle complex cells: if not plain text, parse as node
						if (cellBlock != null && cellBlock.getChildren() != null) {
							List<Block> childBlocks = new ArrayList<>();
							boolean hasComplexContent = false;

							// Collect all child blocks and check for complex content
							for (String childId : cellBlock.getChildren()) {
								Block childBlock = allBlocks.stream().filter(b -> childId.equals(b.getBlockId())).findFirst().orElse(null);

								if (childBlock != null) {
									childBlocks.add(childBlock);
									// Check if complex content (non-plain-text)
									if (isComplexBlock(childBlock)) {
										hasComplexContent = true;
									}
								}
							}

							if (hasComplexContent) {
								// Contains complex content, convert to node structure
								List<DocParseResult> cellNodes = buildHierarchicalStructure(childBlocks, allBlocks, client);
								cell.setNodes(cellNodes);
								cell.setText(""); // Complex cell does not set text
							} else {
								// Plain text content, extract text
								StringBuilder cellContent = new StringBuilder();
								for (Block childBlock : childBlocks) {
									if (childBlock.getText() != null) {
										String childText = extractElementsText(childBlock.getText().getElements());
										if (!childText.isEmpty()) {
											if (cellContent.length() > 0) {
												cellContent.append("\n");
											}
											cellContent.append(childText);
										}
									}
								}
								cell.setText(cellContent.toString());
							}
						} else {
							cell.setText("");
						}

						// If cell has content, mark the row as having valid cells
						if (StringUtils.isNotBlank(cell.getText()) || (cell.getNodes() != null && !cell.getNodes().isEmpty())) {
							hasValidCells = true;
						} else if (position[row][col] == 1) {
							continue; // Skip if current cell is empty and already occupied
						}

						cells.add(cell);

						// Mark all positions occupied by current cell
						for (int r = row; r < Math.min(endRow, rowSize); r++) {
							for (int c = col; c < Math.min(endCol, columnSize); c++) {
								position[r][c] = 1;
							}
						}
					}
				}

				if (hasValidCells) {
					rowData.setCells(cells);
					rows.add(rowData);
				}
			}
		} catch (Exception e) {
			// If conversion fails, return empty list
			log.warn("Error converting table: " + e.getMessage(), e);
		}

		return rows;
	}

	/**
	 * Convert image info
	 *
	 * @param imageBlock Image block object
	 * @param client     LarkClient
	 * @return Image object
	 */
	private static DocParseResult.Image convertImage(Image imageBlock, Client client) {
		DocParseResult.Image image = new DocParseResult.Image();
		image.setType("image_base64");
		image.setBase64(getImageUrl(imageBlock, client));

		return image;
	}

}
