package faang.school.projectservice.exception.handler.controller;

import faang.school.projectservice.dto.ErrorResponse;
import faang.school.projectservice.exception.EntityFieldNotFoundException;
import faang.school.projectservice.exception.JiraException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Validated
@RestController
public class TestController {

    @GetMapping("/jira-exception")
    public void jiraException() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(400)
                .errorMessages(List.of("Jira error occurred"))
                .errors(Map.of("field", "Invalid value"))
                .build();
        throw new JiraException(errorResponse, 400);
    }

    @GetMapping("/entity-field-not-found-exception")
    public void entityFieldNotFoundException() {
        throw new EntityFieldNotFoundException("Field not found");
    }

    @PostMapping("/method-argument-not-valid-exception")
    public String constraintViolationException(
            @RequestParam @NotBlank(message = "must not be blank") String name,
            @RequestParam @Positive(message = "must be greater than or equal to 1") Long id) {
        return name + id;
    }
}

