package faang.school.projectservice.service;

import faang.school.projectservice.dto.jira.CreateJiraDto;
import faang.school.projectservice.dto.jira.ResponseJiraDto;
import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.mapper.jira.JiraMapper;
import faang.school.projectservice.model.Jira;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.JiraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraApiServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JiraRepository jiraRepository;

    @Spy
    private JiraMapper jiraMapper = JiraMapper.INSTANCE;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private JiraApiService jiraApiService;

    @Test
    public void testConnectJira() {
        CreateJiraDto createJiraDto = CreateJiraDto.builder().username("test").build();
        Jira jiraEntity = Jira.builder().id(1L).username("test").build();
        when(jiraRepository.save(any(Jira.class))).thenReturn(jiraEntity);

        ResponseJiraDto response = jiraApiService.connectJira(createJiraDto);

        assertNotNull(response);
        verify(jiraRepository).save(any(Jira.class));
        verify(redisService).saveToRedis(eq("jira_token:" + jiraEntity.getId()), eq(createJiraDto.getToken()));
    }

    @Test
    public void testCreateTask() {
        long projectId = 1L;
        CreateTaskDto createTaskDto = CreateTaskDto.builder().projectId(projectId).build();
        Jira jiraEntity = new Jira(1L, "test", "T1", "test-url",
                Project.builder().id(projectId).build());
        jiraApiService.setIssueCreationUrl("test");

        when(jiraRepository.findByProjectId(projectId)).thenReturn(Optional.of(jiraEntity));
        when(redisService.getFromRedis("jira_token:" + jiraEntity.getId())).thenReturn("test_token");

        ResponseEntity<String> responseEntity = new ResponseEntity<>("response body", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        String response = jiraApiService.createTask(createTaskDto);

        assertEquals("response body", response);
        verify(jiraRepository).findByProjectId(projectId);
        verify(redisService).getFromRedis("jira_token:" + jiraEntity.getId());
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }
}