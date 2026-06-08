package com.ke.bella.openapi.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	private Long cuid;
	private String cuName;
	private Long muid;
	private String muName;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ctime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime mtime;
}
