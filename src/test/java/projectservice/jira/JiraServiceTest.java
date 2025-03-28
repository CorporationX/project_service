package projectservice.jira;

import faang.school.projectservice.client.JiraClient;
import faang.school.projectservice.dto.jira.Fields;
import faang.school.projectservice.dto.jira.filter.AssigneeDto;
import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.request.IssueStatusRequestDto;
import faang.school.projectservice.dto.jira.request.ProjectRequestDto;
import faang.school.projectservice.dto.jira.response.IssuesResponseDto;
import faang.school.projectservice.dto.jira.response.ProjectResponseDto;
import faang.school.projectservice.dto.jira.update.IssueLinkDto;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;
import faang.school.projectservice.dto.jira.update.TransitionDto;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.filter.jira.AssigneeFilter;
import faang.school.projectservice.filter.jira.IssueFilter;
import faang.school.projectservice.filter.jira.IssueStatusFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.jira.JiraServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.service.jira.JiraServiceImpl.NO_APPLICABLE_FILTERS_SET;
import static faang.school.projectservice.service.jira.JiraServiceImpl.PROJECT_DOES_NOT_CONNECTED_TO_JIRA;
import static faang.school.projectservice.util.validation.JiraValidation.FIELD_CANT_BE_NULL;
import static faang.school.projectservice.util.validation.JiraValidation.ISSUE_KEY_CANT_BE_NULL_OR_BLANK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JiraServiceTest {
    @InjectMocks
    private JiraServiceImpl jiraService;

    @Mock
    private ProjectRepository projectRepository;

    private final List<IssueFilter> issueFilters = new ArrayList<>();

    @Spy
    private ProjectMapper projectMapper;

    @Mock
    private JiraClient jiraClient;

    private IssueRequestDto issueRequestDto;
    private IssueUpdateDto issueUpdateDto;

    @BeforeEach
    public void setUp() {
        issueFilters.add(new AssigneeFilter());
        issueFilters.add(new IssueStatusFilter());
        ReflectionTestUtils.setField(jiraService, "issueFilters", issueFilters);

        issueRequestDto = new IssueRequestDto(new Fields());
        issueRequestDto.getFields().setSummary("summary");
        issueRequestDto.getFields().setDescription("description");
        issueRequestDto.getFields().setProject(new ProjectRequestDto("key"));

        issueUpdateDto = new IssueUpdateDto();
        issueUpdateDto.setTransition(new TransitionDto("1"));
        issueUpdateDto.setFields(new IssueUpdateDto.Fields());
        issueUpdateDto.getFields().setIssueLinks(List.of(new IssueLinkDto()));
    }

    @Test
    public void testCreateIssue_callJiraClient() {
        jiraService.createIssue(issueRequestDto);
        verify(jiraClient, times(1)).createIssue(issueRequestDto);
    }

    @Test
    public void testCreateIssue_noJiraClientCall() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraService.createIssue(new IssueRequestDto())
        );
        verify(jiraClient, never()).createIssue(any());
        assertEquals(FIELD_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testUpdateIssue_updateAll() {
        doNothing().when(jiraClient).createIssueLinks(any());
        doNothing().when(jiraClient).setTransitionByKey(anyString(), any());
        doNothing().when(jiraClient).updateIssueByKey(anyString(), any());

        jiraService.updateIssue("key", issueUpdateDto);

        verify(jiraClient, times(1)).createIssueLinks(any());
        verify(jiraClient, times(1)).setTransitionByKey(anyString(), any());
        verify(jiraClient, times(1)).updateIssueByKey(anyString(), any());
    }

    @Test
    public void testUpdateIssue_onlyUpdateIssueByKey() {
        doNothing().when(jiraClient).updateIssueByKey(anyString(), any());
        issueUpdateDto.getFields().setIssueLinks(null);
        issueUpdateDto.setTransition(null);

        jiraService.updateIssue("key", issueUpdateDto);

        verify(jiraClient, never()).createIssueLinks(any());
        verify(jiraClient, never()).setTransitionByKey(anyString(), any());
        verify(jiraClient, times(1)).updateIssueByKey(anyString(), any());
    }

    @Test
    public void testGetAllIssuesWithFilter_allFiltersApplicable() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(Project.builder().jiraKey("key").build()));
        when(jiraClient.getInfoByJql(anyString())).thenReturn(new IssuesResponseDto());
        IssueFilterDto issueFilterDto = new IssueFilterDto();
        issueFilterDto.setStatus(new IssueStatusRequestDto("status"));
        issueFilterDto.setAssignee(new AssigneeDto("name"));

        jiraService.getAllIssuesWithFilter(1L, issueFilterDto);

        verify(jiraClient, times(1)).getInfoByJql(anyString());
    }

    @Test
    public void testGetAllIssuesWithFilter_noApplicableFilters() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(Project.builder().jiraKey("key").build()));
        IssueFilterDto issueFilterDto = new IssueFilterDto();
        issueFilterDto.setStatus(new IssueStatusRequestDto());
        issueFilterDto.setAssignee(new AssigneeDto());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraService.getAllIssuesWithFilter(1L, issueFilterDto)
        );

        verify(jiraClient, never()).getInfoByJql(anyString());
        assertEquals(NO_APPLICABLE_FILTERS_SET, exception.getMessage());
    }

    @Test
    public void testGetAllIssuesWithFilter_projectDoesNotExists() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class,
                () -> jiraService.getAllIssuesWithFilter(1L, null)
        );

        verify(jiraClient, never()).getInfoByJql(anyString());
        assertEquals(PROJECT_DOES_NOT_CONNECTED_TO_JIRA.formatted(1L), exception.getMessage());
    }

    @Test
    public void testGetAllIssuesByProject_projectExists() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(Project.builder().jiraKey("key").build()));
        jiraService.getAllIssuesByProject(1L);
        verify(jiraClient, times(1)).getInfoByJql("project = key");
    }

    @Test
    public void testGetAllIssuesByProject_projectDoesNotExists() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class,
                () -> jiraService.getAllIssuesByProject(1L)
        );

        assertEquals(PROJECT_DOES_NOT_CONNECTED_TO_JIRA.formatted(1L), exception.getMessage());
    }

    @Test
    public void testGetIssueByKey_validKey() {
        jiraService.getIssueByKey("BJS2-66236");
    }

    @Test
    public void testGetIssueByKey_invalidKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jiraService.getIssueByKey(" ")
        );
        assertEquals(ISSUE_KEY_CANT_BE_NULL_OR_BLANK, exception.getMessage());
    }

    @Test
    public void testRegisterProject_validKey() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(new Project()));
        ProjectResponseDto result = jiraService.registerProject(1L, "BJS2");
        assertEquals(projectMapper.toProjectResponseDto(Project.builder().jiraKey("BJS2").build()), result);
    }
}
