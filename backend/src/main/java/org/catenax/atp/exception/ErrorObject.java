package org.catenax.atp.exception;

import lombok.Builder;

import java.util.Map;

@Builder
public record ErrorObject(String code, String message, int status , Map<String, Object> errorParams) {
}
