package projectservice.jira;

import faang.school.projectservice.dto.jira.Fields;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.request.ProjectRequestDto;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;
import faang.school.projectservice.service.jira.JiraServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.projectservice.util.validation.JiraValidation.DESCRIPTION_CANT_BE_NULL;
import static faang.school.projectservice.util.validation.JiraValidation.INVALID_PROJECT_KEY;
import static faang.school.projectservice.util.validation.JiraValidation.ISSUE_KEY_CANT_BE_NULL_OR_BLANK;
import static faang.school.projectservice.util.validation.JiraValidation.PROJECT_CANT_BE_NULL;
import static faang.school.projectservice.util.validation.JiraValidation.PROJECT_KEY_CANT_BE_NULL;
import static faang.school.projectservice.util.validation.JiraValidation.SUMMARY_CANT_BE_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class JiraValidationTest {
    @InjectMocks
    private JiraServiceImpl jiraService;

    private final IssueRequestDto requestDto = new IssueRequestDto();
    private final IssueUpdateDto issueUpdateDto = new IssueUpdateDto();

    @Test
    public void testCreateIssue_allFieldsNull() {
        requestDto.setFields(new Fields());

        Exception exception = assertThrows(Exception.class, () -> jiraService.createIssue(requestDto));

        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains(SUMMARY_CANT_BE_NULL));
        assertTrue(exception.getMessage().contains(DESCRIPTION_CANT_BE_NULL));
        assertTrue(exception.getMessage().contains(PROJECT_CANT_BE_NULL));
    }

    @Test
    public void testCreateIssue_nullProjectKey() {
        requestDto.setFields(new Fields());
        requestDto.getFields().setProject(new ProjectRequestDto());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraService.createIssue(requestDto)
        );

        assertTrue(exception.getMessage().contains(PROJECT_KEY_CANT_BE_NULL));
    }

    @Test
    public void testUpdateIssue_nullIssueKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraService.updateIssue(null, issueUpdateDto)
        );

        assertEquals(ISSUE_KEY_CANT_BE_NULL_OR_BLANK, exception.getMessage());
    }

    @Test
    public void testUpdateIssue_invalidIssueKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraService.updateIssue("   ", issueUpdateDto)
        );

        assertEquals(ISSUE_KEY_CANT_BE_NULL_OR_BLANK, exception.getMessage());
    }

    @Test
    public void testUpdateIssue_nullFields() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraService.updateIssue("   ", issueUpdateDto)
        );

        assertEquals(ISSUE_KEY_CANT_BE_NULL_OR_BLANK, exception.getMessage());
    }

    @Test
    public void testGetIssueByKey_invalidIssueKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraService.getIssueByKey("   ")
        );

        assertEquals(ISSUE_KEY_CANT_BE_NULL_OR_BLANK, exception.getMessage());
    }

    @Test
    public void testRegisterProject_invalidProjectKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraService.registerProject(1L, " JIRA-1234")
        );

        assertEquals(INVALID_PROJECT_KEY, exception.getMessage());
    }
}
