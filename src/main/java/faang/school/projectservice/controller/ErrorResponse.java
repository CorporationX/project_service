package faang.school.projectservice.controller;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
public class ErrorResponse {

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;
}