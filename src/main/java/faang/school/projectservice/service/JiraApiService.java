package faang.school.projectservice.service;

import faang.school.projectservice.dto.jira.CreateJiraDto;
import faang.school.projectservice.dto.jira.payload.Fields;
import faang.school.projectservice.dto.jira.payload.Issuetype;
import faang.school.projectservice.dto.jira.payload.JiraPayload;
import faang.school.projectservice.dto.jira.ResponseJiraDto;
import faang.school.projectservice.dto.jira.payload.ProjectJira;
import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.mapper.jira.JiraMapper;
import faang.school.projectservice.model.Jira;
import faang.school.projectservice.repository.JiraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class JiraApiService {
    private final RestTemplate restTemplate;
    private final JiraRepository jiraRepository;
    private final JiraMapper jiraMapper;
    private final RedisService redisService;
    @Value("${jira.api.issue-url}")
    private String issueCreationUrl;

    @Transactional
    public ResponseJiraDto connectJira(CreateJiraDto createJiraDto) {
        Jira jira = jiraRepository.save(jiraMapper.createDtoToEntity(createJiraDto));
        redisService.saveToRedis("jira_token:" + jira.getId(), createJiraDto.getToken());
        return jiraMapper.entityToResponseDto(jira);
    }

    @Transactional(readOnly = true)
    public Jira getJiraByProjectId(Long projectId) {
        return jiraRepository.findByProjectId(projectId).orElse(null);
    }

    @Async
    public void createTask(CreateTaskDto createTaskDto) {
        Jira jira = getJiraByProjectId(createTaskDto.getProjectId());
        if (jira == null) {
            return;
        }
        Fields fields = Fields.builder().project(ProjectJira.builder().key(jira.getProjectKey()).build())
                .summary(createTaskDto.getName())
                .description( createTaskDto.getDescription())
                .issuetype(new Issuetype("Task"))
                .build();
        JiraPayload jiraPayload = new JiraPayload(fields);

        String username = jira.getUsername();
        String token = redisService.getFromRedis("jira_token:" + jira.getId());

        ResponseEntity<String> response = restTemplate.exchange(jira.getProjectUrl().concat(issueCreationUrl), HttpMethod.POST,
                new HttpEntity<>(jiraPayload, getHeaders(username, token)), String.class);
        log.info(response.getBody());
    }

    public HttpHeaders getHeaders(String username, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = username + ":" + token;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        return headers;
    }
}

