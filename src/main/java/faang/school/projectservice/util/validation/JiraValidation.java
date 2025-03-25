package faang.school.projectservice.util.validation;

import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JiraValidation {

    public static final String SUMMARY_CANT_BE_NULL = "Summary can't be null";
    public static final String DESCRIPTION_CANT_BE_NULL = "Description can't be null";
    public static final String PROJECT_CANT_BE_NULL = "Project can't be null";
    public static final String PROJECT_KEY_CANT_BE_NULL = "Project key can't be null";

    public static void validateIssueCreation(IssueRequestDto issueRequestDto) {
        if (issueRequestDto.getFields().getSummary() == null) {
            log.error(SUMMARY_CANT_BE_NULL);
            throw new IllegalArgumentException(SUMMARY_CANT_BE_NULL);
        }
        if (issueRequestDto.getFields().getDescription() == null) {
            log.error(DESCRIPTION_CANT_BE_NULL);
            throw new IllegalArgumentException(DESCRIPTION_CANT_BE_NULL);
        }
        if (issueRequestDto.getFields().getProject() == null) {
            log.error(PROJECT_CANT_BE_NULL);
            throw new IllegalArgumentException(PROJECT_CANT_BE_NULL);
        }
        if (issueRequestDto.getFields().getProject().getKey() == null) {
            log.error(PROJECT_KEY_CANT_BE_NULL);
            throw new IllegalArgumentException(PROJECT_KEY_CANT_BE_NULL);
        }
    }
}
