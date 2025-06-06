package com.casestudy.inventory_service.Exception;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExceptionResponse {

    private LocalDate timestamp;
    private String message;
    private String details;
    private String httpCodeMessage;

    public ExceptionResponse(LocalDate timestamp, String message, String details, String httpCodeMessage) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.httpCodeMessage = httpCodeMessage;
    }
}
