package com.ke.bella.openapi.protocol.asr;

import com.ke.bella.openapi.common.context.EndpointProcessData;
import com.ke.bella.openapi.config.TaskExecutor;
import com.ke.bella.openapi.common.exception.OneTokenException;
import com.ke.bella.openapi.protocol.Callbacks;
import com.ke.bella.openapi.protocol.log.EndpointLogger;
import com.ke.bella.openapi.utils.DateTimeUtils;
import com.ke.bella.openapi.utils.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class HuoshanStreamLMAsrCallback extends WebSocketListener implements Callbacks.WebSocketCallback {

    // Protocol constants
    private static final byte PROTOCOL_VERSION = 0b0001;
    private static final byte DEFAULT_HEADER_SIZE = 0b0001;
    // Message Type:
    private static final byte FULL_CLIENT_REQUEST = 0b0001;
    private static final byte AUDIO_ONLY_REQUEST = 0b0010;
    private static final byte FULL_SERVER_RESPONSE = 0b1001;
    private static final byte SERVER_ACK = 0b1011;
    private static final byte SERVER_ERROR_RESPONSE = 0b1111;
    // Message Type Specific Flags
    private static final byte NO_SEQUENCE = 0b0000;// no check sequence
    private static final byte POS_SEQUENCE = 0b0001;
    private static final byte NEG_SEQUENCE = 0b0010;
    private static final byte NEG_WITH_SEQUENCE = 0b0011;
    private static final byte NEG_SEQUENCE_1 = 0b0011;
    // Message Serialization
    private static final byte NO_SERIALIZATION = 0b0000;
    private static final byte JSON = 0b0001;
    // Message Compression
    private static final byte NO_COMPRESSION = 0b0000;
    private static final byte GZIP = 0b0001;

    private static final int ERROR_CODE_RATE_LIMIT = 45000292;

    private final HuoshanRealTimeAsrRequest request;
    private final Callbacks.Sender sender;
    private final EndpointProcessData processData;
    private final EndpointLogger logger;
    private final Function<HuoshanLMRealTimeAsrResponse, List<String>> converter;
    private final CompletableFuture<?> startFlag = new CompletableFuture<>();
    private boolean end = false;
    private boolean first = true;
    private boolean isRunning = false;
    private long startTime = DateTimeUtils.getCurrentMills();
    private int audioSequence = 1; // Initialize to 1

    public HuoshanStreamLMAsrCallback(HuoshanRealTimeAsrRequest request, Callbacks.Sender sender, EndpointProcessData processData,
            EndpointLogger logger, Function<HuoshanLMRealTimeAsrResponse, List<String>> converter) {
        this.request = request;
        this.sender = sender;
        this.processData = processData;
        this.logger = logger;
        this.converter = converter;
        processData.setMetrics(new HashMap<>());
    }

    @Data
    // Client request parameter class
    private static class ClientRequest {
        private App app;
        private User user;
        private ModelRequest request;
        private Audio audio;
    }

    @Data
    private static class App {
        private String appid;
        private String cluster;
        private String token;
    }

    @Data
    private static class User {
        private String uid;
    }

    @Data
    private static class ModelRequest {
        private String reqid;
        private boolean show_utterances;
        private String result_type;
        private Integer sequence;
        private String model_name;
        private boolean enable_itn;
        private boolean enable_punc;
        private boolean enable_ddc;
        private Integer vad_segment_duration;
        private Integer end_window_size;
        private Integer force_to_speech_time;
        private Corpus corpus;
    }

    @Data
    private static class Audio {
        private String format;
        private String codec;
        private Integer rate;
        private Integer bits;
        private Integer channel;
    }

    @Data
    private static class Corpus {
        private String boosting_table_id;
        private String context;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        try {
            log.info("WebSocket connection opened");
            sendFullClientRequest(webSocket);
        } catch (Exception e) {
            log.error("Error opening WebSocket", e);
            onError(OneTokenException.fromException(e));
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        log.info("Received text message: {}", text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        try {
            parseResponse(bytes.toByteArray(), webSocket);
        } catch (Exception e) {
            log.error("Error processing WebSocket message", e);
            onError(OneTokenException.fromException(e));
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        complete();
        log.info("WebSocket closing: code={}, reason={}", code, reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        log.info("WebSocket closed: code={}, reason={}", code, reason);
        complete();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        log.error("WebSocket connection failed", t);
        onError(OneTokenException.fromException(t));
    }

    @Override
    public boolean started() {
        try {
            startFlag.get(30, TimeUnit.SECONDS);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw OneTokenException.fromException(e);
        } catch (ExecutionException | TimeoutException e) {
            throw OneTokenException.fromException(e);
        }
    }

    /**
     * Complete processing and close connection
     */
    private void complete() {
        if(!end) {
            processData.getMetrics().put("ttlt", DateTimeUtils.getCurrentMills() - startTime);
            sender.close();
            if(request.isAsync() && logger != null) {
                logger.log(processData);
            }
            end = true;
        }
    }

    /**
     * Handle error
     */
    private void onError(OneTokenException exception) {
        if(!end) {
            end = true;
            sender.onError(exception);
        }
    }

    /**
     * Handle error during processing
     */
    private void onProcessError(OneTokenException exception) {
        if(!end) {
            sender.onError(exception);
        }
    }

    /**
     * Send full client request
     */
    private void sendFullClientRequest(WebSocket webSocket) {
        try {
            byte[] payload = constructFullClientRequest();
            webSocket.send(ByteString.of(payload));
        } catch (Exception e) {
            log.warn("Error sending full client request", e);
            onError(OneTokenException.fromException(e));
        }
    }

    /**
     * Construct full client request
     */
    private byte[] constructFullClientRequest() throws Exception {
        // Build request parameters
        ClientRequest clientRequest = new ClientRequest();

        // Set App info
        App app = new App();
        app.setAppid(request.getAppId());
        app.setCluster(request.getCluster());
        app.setToken(request.getToken());
        clientRequest.setApp(app);

        // Set user info
        User user = new User();
        user.setUid(request.getUid());
        clientRequest.setUser(user);

        // Set request info - LLM specific parameters
        ModelRequest modelRequest = new ModelRequest();
        modelRequest.setReqid(processData.getRequestId());
        modelRequest.setShow_utterances(true);
        modelRequest.setResult_type("single");
        modelRequest.setSequence(1);
        // LLM specific parameters
        modelRequest.setModel_name("bigmodel"); // LLM model name
        modelRequest.setEnable_punc(request.isEnable_punc()); // Whether to enable punctuation
        modelRequest.setEnable_itn(request.isEnable_itn()); // Whether to enable ITN
        modelRequest.setEnable_ddc(false); 	// Smoothing disabled by default

        // Set corpus and hot words
        Corpus corpus = new Corpus();
        if(StringUtils.isNotBlank(request.getHotWords())) {
            corpus.setContext(buildHotWords(request.getHotWords()));
        }
        if(StringUtils.isNotBlank(request.getHotWordsTableId())) {
            corpus.setBoosting_table_id(request.getHotWordsTableId());
        }
        modelRequest.setCorpus(corpus);

        clientRequest.setRequest(modelRequest);

        // Set audio info
        Audio audio = new Audio();
        audio.setFormat(request.getFormat());
        audio.setRate(request.getSampleRate());
        audio.setBits(16); // Default 16
        audio.setChannel(1); // Default mono
        audio.setCodec("raw"); // Default raw(PCM)
        clientRequest.setAudio(audio);

        // Convert parameters to JSON
        byte[] jsonPayload = JacksonUtils.toByte(clientRequest);

        // GZIP compress
        byte[] compressedPayload = gzipCompress(jsonPayload);

        // Build header
        byte[] header = getHeader(FULL_CLIENT_REQUEST, POS_SEQUENCE, JSON, GZIP, (byte) 0);

        // Build sequence number
        byte[] seqBytes = intToBytes(audioSequence);

        // Build payload length bytes
        byte[] payloadSizeBytes = intToBytes(compressedPayload.length);

        // Concatenate header, sequence number, payload length and payload
        byte[] fullClientRequest = new byte[header.length + seqBytes.length + payloadSizeBytes.length + compressedPayload.length];
        int destPos = 0;
        System.arraycopy(header, 0, fullClientRequest, destPos, header.length);
        destPos += header.length;
        System.arraycopy(seqBytes, 0, fullClientRequest, destPos, seqBytes.length);
        destPos += seqBytes.length;
        System.arraycopy(payloadSizeBytes, 0, fullClientRequest, destPos, payloadSizeBytes.length);
        destPos += payloadSizeBytes.length;
        System.arraycopy(compressedPayload, 0, fullClientRequest, destPos, compressedPayload.length);

        return fullClientRequest;
    }

    /**
     * Send audio data
     */
    public void sendAudioData(WebSocket webSocket, byte[] audioData, boolean isLast) {
        try {
            audioSequence++;

            // If it is the last chunk, set sequence number to negative
            int seq = audioSequence;
            if(isLast) {
                seq = -seq;
            }

            // Construct audio data payload
            byte messageTypeSpecificFlags = isLast ? NEG_WITH_SEQUENCE : POS_SEQUENCE;

            // Build header
            byte[] header = getHeader(AUDIO_ONLY_REQUEST, messageTypeSpecificFlags, JSON, GZIP, (byte) 0);

            // Build sequence number
            byte[] sequenceBytes = intToBytes(seq);

            // Compress audio data
            byte[] compressedAudio = gzipCompress(audioData);

            // Build payload length bytes
            byte[] payloadSizeBytes = intToBytes(compressedAudio.length);

            // Concatenate header, sequence number, payload length and payload
            byte[] audioOnlyRequest = new byte[header.length + sequenceBytes.length + payloadSizeBytes.length + compressedAudio.length];
            int destPos = 0;
            System.arraycopy(header, 0, audioOnlyRequest, destPos, header.length);
            destPos += header.length;
            System.arraycopy(sequenceBytes, 0, audioOnlyRequest, destPos, sequenceBytes.length);
            destPos += sequenceBytes.length;
            System.arraycopy(payloadSizeBytes, 0, audioOnlyRequest, destPos, payloadSizeBytes.length);
            destPos += payloadSizeBytes.length;
            System.arraycopy(compressedAudio, 0, audioOnlyRequest, destPos, compressedAudio.length);

            webSocket.send(ByteString.of(audioOnlyRequest));
        } catch (Exception e) {
            onProcessError(OneTokenException.fromException(e));
        }
    }

    /**
     * Send audio data in chunks
     */
    public void sendAudioDataInChunks(WebSocket webSocket, byte[] audioData, int chunkSize, int intervalMs) {
        TaskExecutor.submit(() -> {
            try {
                int offset = 0;
                while (offset < audioData.length) {
                    int remaining = audioData.length - offset;
                    int length = Math.min(chunkSize, remaining);
                    byte[] chunk = new byte[length];
                    System.arraycopy(audioData, offset, chunk, 0, length);

                    boolean isLast = (offset + length >= audioData.length);
                    sendAudioData(webSocket, chunk, isLast);

                    offset += length;

                    if(!isLast && intervalMs > 0) {
                        Thread.sleep(intervalMs);
                    }
                }
            } catch (Exception e) {
                log.error("Error sending audio chunk data", e);
                onProcessError(OneTokenException.fromException(e));
            }
        });
    }

    /**
     * Parse server response
     * Follows official sample logic: parse structure first, decompress at the end
     */
    private void parseResponse(byte[] message, WebSocket webSocket) {
        if(message == null || message.length == 0) {
            log.warn("Received empty message");
            return;
        }

        // Parse header - following official sample logic
        int headerSize = message[0] & 0x0f; // Read headerSize from first byte of header
        int headerLen = headerSize * 4; // Actual header length
        int messageType = (message[1] & 0xf0) >> 4;
        int messageTypeFlag = message[1] & 0x0f;
        int messageSerial = (message[2] & 0xf0) >> 4;
        int messageCompress = message[2] & 0x0f;

        // Extract remaining data starting from headerLen (payload area)
        if(message.length < headerLen) {
            log.error("Insufficient message length, cannot parse header: messageLength={}, headerLen={}", message.length, headerLen);
            return;
        }

        byte[] payload = Arrays.copyOfRange(message, headerLen, message.length);

        // Dynamically parse sequence number and other fields based on messageTypeSpecificFlags (following official sample)
        boolean isLastPackage = false;

        if((messageTypeFlag & 0x01) != 0) {
            // Has sequence number
            if(payload.length < 4) {
                log.error("Insufficient message length, cannot parse sequence number: payloadLength={}", payload.length);
                return;
            }
            payload = Arrays.copyOfRange(payload, 4, payload.length);
        }

        if((messageTypeFlag & 0x02) != 0) {
            // Is last package
            isLastPackage = true;
        }

        if((messageTypeFlag & 0x04) != 0) {
            // Has event field
            if(payload.length < 4) {
                log.error("Insufficient message length, cannot parse event: payloadLength={}", payload.length);
                return;
            }
            payload = Arrays.copyOfRange(payload, 4, payload.length);
        }

        if(messageType == SERVER_ERROR_RESPONSE) {
            // SERVER_ERROR_RESPONSE: errorCode(4) + payloadSize(4)
            if(payload.length < 8) {
                log.error("Error message length insufficient: payloadLength={}", payload.length);
                return;
            }
            int errorCode = bytesToInt(Arrays.copyOfRange(payload, 0, 4));
            int actualPayloadSize = bytesToInt(Arrays.copyOfRange(payload, 4, 8));
            payload = Arrays.copyOfRange(payload, 8, payload.length);

            // Extract actual payload data
            if(actualPayloadSize > 0 && payload.length >= actualPayloadSize) {
                payload = Arrays.copyOfRange(payload, 0, actualPayloadSize);

                // Decompress and handle error
                if(messageCompress == GZIP) {
                    payload = gzipDecompress(payload);
                }

                String errorMsg = new String(payload);
                log.error("Server error: code={}, message={}", errorCode, errorMsg);
                handleTranscriptionFailed(mapErrorCodeToHttpStatus(errorCode), errorMsg);
            }
        } else if(messageType == FULL_SERVER_RESPONSE) {
            // FULL_SERVER_RESPONSE: payloadSize at start of payload (sequence number already skipped)
            if(payload.length < 4) {
                log.error("Insufficient message length, cannot parse payloadSize: payloadLength={}", payload.length);
                return;
            }
            int actualPayloadSize = bytesToInt(Arrays.copyOfRange(payload, 0, 4));
            payload = Arrays.copyOfRange(payload, 4, payload.length);

            // Extract actual payload data
            if(actualPayloadSize > 0) {
                if(payload.length < actualPayloadSize) {
                    log.error("Payload length insufficient: payloadLength={}, expectedSize={}", payload.length, actualPayloadSize);
                    return;
                }
                payload = Arrays.copyOfRange(payload, 0, actualPayloadSize);
            }

            // Decompress at the end (following official sample order)
            if(messageCompress == GZIP && payload.length > 0) {
                payload = gzipDecompress(payload);
            }

            // Handle FULL_SERVER_RESPONSE message
            if(payload.length > 0) {
                // Parse JSON response
                HuoshanLMRealTimeAsrResponse response = JacksonUtils.deserialize(payload, HuoshanLMRealTimeAsrResponse.class);

                // Check response status
                if(response == null) {
                    log.error("LLM ASR response error: {}", new String(payload));
                    handleTranscriptionFailed(503, new String(payload));
                    return;
                }

                // Handle response
                if(!isRunning) {
                    // First response, set running flag
                    isRunning = true;

                    // Non-stream request sends file directly, stream request is sent by client
                    if(!request.isAsync()) {
                        sendAudioDataInChunks(webSocket, request.getAudioData(), request.getChunkSize(), request.getIntervalMs());
                    } else {
                        startFlag.complete(null);
                    }
                }

                // Check response code
                if(response.getCode() != 0) {
                    log.error("LLM ASR response error: code={}, message={}", response.getCode(), response.getMessage());
                    handleTranscriptionFailed(getHttpCode(response.getCode()), response.getMessage());
                    return;
                }

                // Determine if it's an intermediate or final response
                if(isLastPackage) {
                    response.setCompletion(true);
                    handleFinalResponse(response);
                } else {
                    handleIntermediateResponse(response);
                }
            }
        } else {
            log.warn("Received unknown message type: messageType={}, messageLength={}", messageType, message.length);
        }
    }

    /**
     * Handle intermediate response
     */
    private void handleIntermediateResponse(HuoshanLMRealTimeAsrResponse response) {
        try {
            if(converter != null) {
                List<String> results = converter.apply(response);
                if(results != null && !results.isEmpty()) {
                    for (String result : results) {
                        sender.send(result);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing intermediate response", e);
        }
    }

    /**
     * Handle final response
     */
    private void handleFinalResponse(HuoshanLMRealTimeAsrResponse response) {
        try {
            if(converter != null) {
                List<String> results = converter.apply(response);
                if(results != null && !results.isEmpty()) {
                    for (String result : results) {
                        sender.send(result);
                    }
                }
            }
            complete();
        } catch (Exception e) {
            log.error("Error processing final response", e);
            onError(OneTokenException.fromException(e));
        }
    }

    /**
     * Handle transcription failed event
     */
    private void handleTranscriptionFailed(int code, String errorMsg) {
        onError(new OneTokenException.ChannelException(code, errorMsg));
    }

    /**
     * Get HTTP status code
     */
    private int getHttpCode(int code) {
        return code == 0 ? 200 : 500;
    }

    /**
     * Map Volcano Engine error code to HTTP status code
     */
    private int mapErrorCodeToHttpStatus(int errorCode) {
        if(errorCode == ERROR_CODE_RATE_LIMIT) {
            return 429;
        }

        if(errorCode >= 40000000 && errorCode < 50000000) {
            return 400;
        } else if(errorCode >= 50000000 && errorCode < 60000000) {
            return 500;
        }

        return 500;
    }

    /**
     * Get message header
     */
    private byte[] getHeader(byte messageType, byte messageTypeSpecificFlags, byte serialMethod, byte compressionType,
            byte reservedData) {
        final byte[] header = new byte[4];
        header[0] = (PROTOCOL_VERSION << 4) | DEFAULT_HEADER_SIZE; // Protocol
                                                                   // version|header
                                                                   // size
        header[1] = (byte) ((messageType << 4) | messageTypeSpecificFlags); // message
                                                                            // type
                                                                            // |
                                                                            // messageTypeSpecificFlags
        header[2] = (byte) ((serialMethod << 4) | compressionType);
        header[3] = reservedData;
        return header;
    }

    /**
     * Integer to byte array
     */
    private byte[] intToBytes(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * Byte array to integer
     */
    private int bytesToInt(byte[] src) {
        if(src == null || (src.length != 4)) {
            throw new IllegalArgumentException("Invalid byte array for int conversion");
        }
        return ((src[0] & 0xFF) << 24)
                | ((src[1] & 0xff) << 16)
                | ((src[2] & 0xff) << 8)
                | ((src[3] & 0xff));
    }

    /**
     * GZIP compression
     */
    private byte[] gzipCompress(byte[] src) {
        if(src == null || src.length == 0) {
            src = new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(src);
        } catch (IOException e) {
            log.error("GZIP compression failed", e);
        } finally {
            if(gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    log.error("Failed to close GZIP output stream", e);
                }
            }
        }
        return out.toByteArray();
    }

    /**
     * GZIP decompression
     */
    private byte[] gzipDecompress(byte[] src) {
        if(src == null || src.length == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(src);
        GZIPInputStream gzip = null;
        try {
            gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int n;
            while ((n = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            log.error("GZIP decompression failed", e);
        } finally {
            if(gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    log.error("Failed to close GZIP input stream", e);
                }
            }
        }
        return out.toByteArray();
    }

    /**
     * Convert comma-separated hot word string to JSON format
     * Input: "hotword1,hotword2"
     * Output: {"hotwords":[{"word":"hotword1"}, {"word":"hotword2"}]}
     */
    private String buildHotWords(String hotWords) {
        if(StringUtils.isBlank(hotWords)) {
            return null;
        }

        // Use Stream API to simplify processing
        List<HotWord> hotWordList = Arrays.stream(hotWords.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(HotWord::new)
                .collect(Collectors.toList());

        if(hotWordList.isEmpty()) {
            return null;
        }

        return JacksonUtils.serialize(new HotWordsWrapper(hotWordList));
    }

    @Data
    @AllArgsConstructor
    private static class HotWordsWrapper {
        private final List<HotWord> hotwords;
    }

    @Data
    @AllArgsConstructor
    private static class HotWord {
        private final String word;
    }
}
