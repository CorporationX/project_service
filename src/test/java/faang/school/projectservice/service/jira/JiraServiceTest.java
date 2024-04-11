package faang.school.projectservice.service.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.client.jira.JiraClient;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueTypeDto;
import faang.school.projectservice.dto.jira.JiraAccountDto;
import faang.school.projectservice.mapper.jira.IssueMapper;
import faang.school.projectservice.mapper.jira.IssueTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraServiceTest {

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private IssueMapper issueMapper;
    @Mock
    private IssueTypeMapper issueTypeMapper;
    @Mock
    private JiraClient jiraClient;
    @InjectMocks
    private JiraService jiraService;

    private Issue issue;
    private IssueType issueType;
    private IssueDto issueDto;
    private JiraAccountDto jiraAccountDto;

    @BeforeEach
    void setUp() {
        issueDto = IssueDto.builder()
                .key("BJS2-1717")
                .projectKey("BJS2")
                .issueType(IssueTypeDto.builder()
                        .id(1L)
                        .name("Story")
                        .description("User's story")
                        .build())
                .summary("Jira integration")
                .description("Suffer but do it")
                .build();
        jiraAccountDto = JiraAccountDto.builder()
                .projectUrl("https://faang-school.atlassian.net/")
                .username("hhzktoeto")
                .password("password")
                .build();
        issue = new Issue(issueDto.getSummary(), null, issueDto.getKey(), null, null,
                null, null, issueDto.getDescription(), null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null);
        issueType = new IssueType(null, issueDto.getIssueType().getId(), issueDto.getIssueType().getName(), true,
                issueDto.getIssueType().getDescription(), null);
    }

    @Test
    void createIssue_IssueCreated_ThenReturnedAsDto() {
        whenUserServiceClientGetJiraAccountInfo();
        when(jiraClient.createIssue(any(IssueInput.class))).thenReturn(issueDto.getKey());
        when(issueTypeMapper.toEntity(any(IssueTypeDto.class))).thenReturn(issueType);

        String returned = jiraService.createIssue(issueDto);

        assertAll(
                () -> verify(userServiceClient, times(1)).getJiraAccountInfo(),
                () -> verify(jiraClient, times(1)).createIssue(any(IssueInput.class)),
                () -> assertEquals(issueDto.getKey(), returned)
        );
    }

    @Test
    void getIssue_IssueFound_ThenReturnedAsDto() {
        whenUserServiceClientGetJiraAccountInfo();
        when(jiraClient.getIssue(anyString())).thenReturn(issue);
        when(issueMapper.toDto(any(Issue.class))).thenReturn(issueDto);

        IssueDto returned = jiraService.getIssue(issueDto.getKey());

        assertAll(
                () -> verify(userServiceClient, times(1)).getJiraAccountInfo(),
                () -> verify(jiraClient, times(1)).getIssue(anyString()),
                () -> assertEquals(issueDto, returned)
        );
    }

    @Test
    void getAllIssues_IssuesFound_ThenReturnedAsDto() {
        whenUserServiceClientGetJiraAccountInfo();
        when(jiraClient.getAllIssues(anyString())).thenReturn(List.of(issue));
        when(issueMapper.toDto(anyList())).thenReturn(List.of(issueDto));

        List<IssueDto> returned = jiraService.getAllIssues(issueDto.getProjectKey());

        assertAll(
                () -> verify(userServiceClient, times(1)).getJiraAccountInfo(),
                () -> verify(jiraClient, times(1)).getAllIssues(issueDto.getProjectKey()),
                () -> verify(issueMapper, times(1)).toDto(anyList()),
                () -> assertEquals(List.of(issueDto), returned)
        );
    }

    @Test
    void getIssuesByStatusId_IssuesFound_ThenReturnedAsDto() {
        whenUserServiceClientGetJiraAccountInfo();
        when(jiraClient.getIssuesByStatusId(anyString(), anyLong())).thenReturn(List.of(issue));
        when(issueMapper.toDto(anyList())).thenReturn(List.of(issueDto));

        List<IssueDto> returned = jiraService.getIssuesByStatusId(issueDto.getProjectKey(), 3L);

        assertAll(
                () -> verify(userServiceClient, times(1)).getJiraAccountInfo(),
                () -> verify(jiraClient, times(1)).getIssuesByStatusId(issueDto.getProjectKey(), 3L),
                () -> verify(issueMapper, times(1)).toDto(anyList()),
                () -> assertEquals(List.of(issueDto), returned)
        );
    }

    @Test
    void getIssuesByAssigneeId_IssuesFound_ThenReturnedAsDto() {
        whenUserServiceClientGetJiraAccountInfo();
        when(jiraClient.getIssuesByAssigneeId(anyString(), anyString())).thenReturn(List.of(issue));
        when(issueMapper.toDto(anyList())).thenReturn(List.of(issueDto));

        List<IssueDto> returned = jiraService.getIssuesByAssigneeId(issueDto.getProjectKey(), "AssigneeId");

        assertAll(
                () -> verify(userServiceClient, times(1)).getJiraAccountInfo(),
                () -> verify(jiraClient, times(1))
                        .getIssuesByAssigneeId(issueDto.getProjectKey(), "AssigneeId"),
                () -> verify(issueMapper, times(1)).toDto(anyList()),
                () -> assertEquals(List.of(issueDto), returned)
        );
    }

    private void whenUserServiceClientGetJiraAccountInfo() {
        when(userServiceClient.getJiraAccountInfo()).thenReturn(jiraAccountDto);
    }
}
