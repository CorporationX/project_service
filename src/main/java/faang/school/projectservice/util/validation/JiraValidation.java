package faang.school.projectservice.util.validation;

import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JiraValidation {
    public static final String SUMMARY_CANT_BE_NULL = "Summary can't be null";
    public static final String DESCRIPTION_CANT_BE_NULL = "Description can't be null";
    public static final String PROJECT_CANT_BE_NULL = "Project can't be null";
    public static final String PROJECT_KEY_CANT_BE_NULL = "Project key can't be null";
    public static final String ISSUE_KEY_CANT_BE_NULL_OR_BLANK = "Issue key can't be null or blank";
    public static final String INVALID_PROJECT_KEY = "Invalid project key";

    public static void validateCreateIssue(IssueRequestDto issueRequestDto) {
        List<String> errors = new ArrayList<>();

        if (issueRequestDto.getFields().getSummary() == null) {
            errors.add(SUMMARY_CANT_BE_NULL);
        }
        if (issueRequestDto.getFields().getDescription() == null) {
            errors.add(DESCRIPTION_CANT_BE_NULL);
        }
        if (issueRequestDto.getFields().getProject() == null) {
            errors.add(PROJECT_CANT_BE_NULL);
        }
        if (issueRequestDto.getFields().getProject() != null
                && issueRequestDto.getFields().getProject().getKey() == null) {
            errors.add(PROJECT_KEY_CANT_BE_NULL);
        }

        if (!errors.isEmpty()) {
            String errorMessage = String.join(". ", errors);
            log.error("Validation errors: {}", errorMessage);
            throw new IllegalArgumentException("Validation errors: " + errorMessage);
        }
    }

    public static void validateIssueKey(String key) {
        if (key == null || key.isBlank()) {
            log.error(ISSUE_KEY_CANT_BE_NULL_OR_BLANK);
            throw new IllegalArgumentException(ISSUE_KEY_CANT_BE_NULL_OR_BLANK);
        }
    }

    public static void validateProjectKey(String key) {
        if (key == null || key.length() < 3 || key.charAt(0) == ' ') {
            log.error(INVALID_PROJECT_KEY);
            throw new IllegalArgumentException(INVALID_PROJECT_KEY);
        }
    }
}