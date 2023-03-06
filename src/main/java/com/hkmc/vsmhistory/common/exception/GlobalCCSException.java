package com.hkmc.vsmhistory.common.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalCCSException extends RuntimeException {

  private static final long serialVersionUID = -96044579582852997L;

  private static final String CCS_EXCEPTION_MESASGE = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

  private final int code;

  private final String errorMessage;

  private final HttpStatus status;

  private final HttpHeaders httpHeaders;

  private final String body;

  public GlobalCCSException() {
    super(CCS_EXCEPTION_MESASGE);
    this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
    this.errorMessage = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
    this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    this.httpHeaders = new HttpHeaders();
    this.body = StringUtils.EMPTY;
  }

  public GlobalCCSException(final int code) {
    super(CCS_EXCEPTION_MESASGE);
    this.code = code;
    this.status = this.getHttpStatus(code);
    this.errorMessage = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
    this.httpHeaders = new HttpHeaders();
    this.body = StringUtils.EMPTY;
  }

  public GlobalCCSException(final int code, final String ccsMessage) {
    super(ccsMessage);
    this.code = code;
    this.errorMessage = ccsMessage;
    this.status = this.getHttpStatus(code);
    this.httpHeaders = new HttpHeaders();
    this.body = StringUtils.EMPTY;
  }

  public GlobalCCSException(String message, int code, String ccsMessage) {
    super(message);
    this.code = code;
    this.errorMessage = ccsMessage;
    this.status = this.getHttpStatus(code);
    this.httpHeaders = new HttpHeaders();
    this.body = StringUtils.EMPTY;
  }

  public GlobalCCSException(String message, int code, String ccsMessage, HttpHeaders httpHeaders, String body) {
    super(message);
    this.code = code;
    this.errorMessage = ccsMessage;
    this.status = this.getHttpStatus(code);
    this.httpHeaders = httpHeaders;
    this.body = body;
  }

  public HttpStatus getHttpStatus(final int code) {
    try {
      return HttpStatus.valueOf(code);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

}