package org.catenax.atp.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class BadDataException extends RuntimeException {

    private String code;
    private String message;
    public BadDataException(String message) {
        this.message=message;
    }

    public BadDataException(String message, String code) {
        this.message=message;
        this.code= code;
    }
}
