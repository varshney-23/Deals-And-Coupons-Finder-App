package com.casestudy.inventory_service.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExceptionResponse {

    private LocalDate timestamp;
    private String message;
    private String details;
    private String httpCodeMessage;

    public void ExceptionResponse(LocalDate timestamp, String message, String details, String httpCodeMessage) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.httpCodeMessage = httpCodeMessage;
    }
}
