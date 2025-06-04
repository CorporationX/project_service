package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.task.JiraCreateIssueJSON;
import faang.school.projectservice.dto.jira.task.JiraCreateTaskDto;
import faang.school.projectservice.dto.jira.task.JiraChangeTaskDto;
import faang.school.projectservice.dto.jira.task.JiraTaskFilterDto;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final RestTemplate jiraRestTemplate;

    @Value("${jira.base-url}")
    private String baseUrl;
    private final String jiraRestApiUrl = "/rest/api/3";


//        Map<String, Object> requestBody = new HashMap<>();
//        Map<String, Object> fields = new HashMap<>();
//
//        Map<String, Object> project = new HashMap<>();
//        project.put("id", jiraCreateTaskDto.getProjectId());
//
//        Map<String, Object> issueType = new HashMap<>();
//        issueType.put("id", jiraCreateTaskDto.getIssueType());
//
//        // Основные поля задачи
//        fields.put("summary", jiraCreateTaskDto.getSummary());
//        fields.put("issuetype", issueType);
//        fields.put("project", project);
//
//        // Do check before adding? Not required fields
//        Map<String, Object> assignee = new HashMap<>();
//        assignee.put("id", jiraCreateTaskDto.getAssigneeId());
//
//        Map<String, Object> parentKey = new HashMap<>();
//        assignee.put("key", jiraCreateTaskDto.getParentKey());
//
//        fields.put("assignee", assignee);
//        fields.put("parent", parentKey);
//
//        requestBody.put("fields", fields);

    @Override
    public Map createIssue(JiraCreateTaskDto jiraCreateTaskDto) {
        String url = new StringBuilder(baseUrl)
                .append(jiraRestApiUrl)
                .append("/issue")
                .toString();
        log.info(jiraCreateTaskDto.getFields().toString());
        try {
            ResponseEntity<Map> response = jiraRestTemplate.postForEntity(
                    url,
                    jiraCreateTaskDto.getFields(),
                    Map.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public Map<String, Object> createIssueWithJSON(Map json) {
        String url = new StringBuilder(baseUrl)
                .append(jiraRestApiUrl)
                .append("/issue")
                .toString();

        try {
            ResponseEntity<Map> response = jiraRestTemplate.postForEntity(
                    url,
                    json,
                    Map.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public Map<String, Object> changeIssue(long issueId, JiraChangeTaskDto jiraChangeTaskDto) {
        String url = new StringBuilder(baseUrl)
                .append(jiraRestApiUrl)
                .append("/issue")
                .toString();

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> fields = new HashMap<>();

        Map<String, Object> status = new HashMap<>();
        status.put("self", jiraChangeTaskDto.getStatus());
        status.put("description", jiraChangeTaskDto.getStatus());
        status.put("iconUrl", jiraChangeTaskDto.getStatus());
        status.put("name", jiraChangeTaskDto.getStatus());
        status.put("id", jiraChangeTaskDto.getStatus());
        // Можно взять из запроса: https://faang-school.atlassian.net/rest/api/3/status найти нужный по ID + ID надо будет настроить в ручную в Enum тк они не совпадают с 1,2,3...6
        Map<String, Object> statusCategory = new HashMap<>();
        statusCategory.put("self", jiraChangeTaskDto.getStatus());
        statusCategory.put("id", jiraChangeTaskDto.getStatus());
        statusCategory.put("key", jiraChangeTaskDto.getStatus());
        statusCategory.put("colorName", jiraChangeTaskDto.getStatus());
        statusCategory.put("name", jiraChangeTaskDto.getStatus());
        status.put("statusCategory", statusCategory);

        Map<String, Object> assignee = new HashMap<>();
        assignee.put("self", jiraChangeTaskDto.getAssigneeId());
        assignee.put("accountId", jiraChangeTaskDto.getAssigneeId());
        assignee.put("emailAddress", jiraChangeTaskDto.getAssigneeId());
        Map<String, Object> avatarUrls = new HashMap<>();
        avatarUrls.put("48x48", jiraChangeTaskDto.getAssigneeId());
        avatarUrls.put("48x48", jiraChangeTaskDto.getAssigneeId());
        avatarUrls.put("48x48", jiraChangeTaskDto.getAssigneeId());
        avatarUrls.put("48x48", jiraChangeTaskDto.getAssigneeId());
        assignee.put("avatarUrls", avatarUrls);
        assignee.put("displayName", jiraChangeTaskDto.getAssigneeId());
        assignee.put("active", jiraChangeTaskDto.getAssigneeId());
        assignee.put("timeZone", jiraChangeTaskDto.getAssigneeId());
        assignee.put("accountType", jiraChangeTaskDto.getAssigneeId());

        Map<String, Object> parent = new HashMap<>();
        parent.put("key", jiraChangeTaskDto.getParentKey());

        List<Map<String, Object>> subTasks = new ArrayList<>(); // Получается я должен вытащить все таски по taskId и заполнить список содержимым тасок?

        // Основные поля задачи
        fields.put("id", jiraChangeTaskDto.getIssuesId() != null ? jiraChangeTaskDto.getIssuesId() : issueId);
        fields.put("summary", jiraChangeTaskDto.getSummary());
        fields.put("status", status); // тут должен быть JSON
        fields.put("duedate", jiraChangeTaskDto.getDuedate());
        fields.put("assignee", assignee);
        fields.put("parent", parent);
        fields.put("subtasks", subTasks); // тут массив

        requestBody.put("fields", fields);
        try {
            ResponseEntity<Map> response = jiraRestTemplate.postForEntity(
                    url,
                    requestBody,
                    Map.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public Map<String, Object> getAllIssuesWithFilter(String projectKey, JiraTaskFilterDto jiraTaskFilterDto) {
        String jql = new StringBuilder("project=")
                .append(projectKey)
                .append("")
                .toString();
        String url = new StringBuilder(baseUrl)
                .append(jiraRestApiUrl)
                .append("/search?jql=project=")
                .append(projectKey)
                .append("+and+")
                .toString();
        try {
            ResponseEntity<Map> response = jiraRestTemplate.getForEntity(
                    url,
                    Map.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public Map<String, Object> getAllIssues(String projectKey) {
        String url = new StringBuilder(baseUrl)
                .append(jiraRestApiUrl)
                .append("/search?jql=project=")
                .append(projectKey)
                .toString();
        try {
            ResponseEntity<Map> response = jiraRestTemplate.getForEntity(
                    url,
                    Map.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public Map getIssueById(long issueId) {
        String url = new StringBuilder(baseUrl)
                .append(jiraRestApiUrl)
                .append("/issue/")
                .append(issueId)
                .toString();
        try {
            ResponseEntity<Map> response = jiraRestTemplate.getForEntity(
                    url,
                    Map.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }


}
