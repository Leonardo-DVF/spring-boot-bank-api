package br.com.bank.bankapi.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard error response returned by the API.")
public record ApiError(

        @Schema(description = "Timestamp when the error occurred.", example = "2026-04-07T12:00:00Z")
        Instant timestamp,

        @Schema(description = "HTTP status code.", example = "400")
        int status,

        @Schema(description = "HTTP error name.", example = "Bad Request")
        String error,

        @Schema(description = "Detailed error message.", example = "Invalid request data")
        String message,

        @Schema(description = "Request path that caused the error.", example = "/accounts")
        String path,

        @Schema(description = "Validation errors by field.")
        Map<String, String> fieldErrors
) {}