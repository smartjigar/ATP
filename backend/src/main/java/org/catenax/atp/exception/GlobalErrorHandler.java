package org.catenax.atp.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {




    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorObject> validatorExceptionHandle(MethodArgumentNotValidException ex){
        Map<String, Object> errors = ex.getBindingResult().getFieldErrors()
                .stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));


        return new ResponseEntity<>(
                ErrorObject.builder()
                        .message(ex.getBody().getDetail())
                        .errorParams(errors)
                        .code("ATP-01-02")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({ ConstraintViolationException.class})
    public ResponseEntity<ErrorObject> constrainViolationExceptionHandle(ConstraintViolationException ex){
        return new ResponseEntity<>(
                ErrorObject.builder()
                        .message(ex.getLocalizedMessage())
                        .errorParams(null)
                        .code("ATP-01-01")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadDataException.class})
    public ResponseEntity<ErrorObject> badDataExceptionHandle(BadDataException ex){
        return new ResponseEntity<>(
                ErrorObject.builder()
                        .message(ex.getLocalizedMessage())
                        .code(ex.getCode()!= null ? ex.getCode() :  "ATP-05-01")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorObject> globalHandler(Exception ex){
        ex.printStackTrace();
        return new ResponseEntity<>(
                ErrorObject.builder()
                        .message(ex.getLocalizedMessage())
                        .code("ATP-00")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build(), HttpStatus.BAD_REQUEST);
    }

}
