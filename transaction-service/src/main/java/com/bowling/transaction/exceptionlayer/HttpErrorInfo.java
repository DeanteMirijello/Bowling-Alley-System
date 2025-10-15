package com.bowling.transaction.exceptionlayer;

import lombok.*;
import org.springframework.http.HttpStatus;
import java.time.ZonedDateTime;

@Getter
public class HttpErrorInfo {

    //private final ZonedDateTime timestamp;
    private final String path;
    private final HttpStatus httpStatus;
    private final String message;

    public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {

        //this.timestamp = ZonedDateTime.now();
        this.httpStatus = httpStatus;
        this.path = path;
        this.message = message;
    }
}


