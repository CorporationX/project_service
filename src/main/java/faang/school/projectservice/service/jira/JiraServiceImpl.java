package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.task.JiraIssueFilterDto;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
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

    @Override
    public Map<String, Object> createIssue(Map body) {
        String url = new StringBuilder(baseUrl)
                .append(jiraRestApiUrl)
                .append("/issue")
                .toString();

        try {
            ResponseEntity<Map> response = jiraRestTemplate.postForEntity(
                    url,
                    body,
                    Map.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public Map<String, Object> changeIssue(long issueId, Map body) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(jiraRestApiUrl)
                .path("/issue/")
                .path(String.valueOf(issueId))
                .queryParam("returnIssue", true)
                .build()
                .toUri();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = jiraRestTemplate.exchange(
                    uri,
                    HttpMethod.PUT,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                log.info("Task {} updated successfully and full issue data returned. Status: {}",
                        issueId,
                        response.getStatusCode());

                return response.getBody();
            } else {
                log.warn("Task {} updated, but no body or unexpected status: {}", issueId, response.getStatusCode());
                return body;
            }
        } catch (HttpClientErrorException e) {
            log.error("Error updating task {}. Response Body: {}", issueId, e.getResponseBodyAsString(), e);
            throw e;
        }
    }

    @Override
    public Map<String, Object> getAllIssuesWithFilter(String projectKey,
                                                      JiraIssueFilterDto jiraIssueFilterDto,
                                                      int startAt,
                                                      int maxResults,
                                                      Integer limit) {
        int total;
        int currentStartAt = startAt;
        Map<String, Object> allIssues = new LinkedHashMap<>();

        try {
            do {
                StringBuilder jql = new StringBuilder("project=")
                        .append(projectKey);

                if (jiraIssueFilterDto.getStatus() != null) {
                    jql.append("+AND+")
                            .append("status=")
                            .append(jiraIssueFilterDto.getStatus().ordinal());
                }
                if (jiraIssueFilterDto.getAssignee() != null && !jiraIssueFilterDto.getAssignee().isEmpty()) {
                    jql.append("+AND+")
                            .append("assignee=")
                            .append(jiraIssueFilterDto.getAssignee());
                }

                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + jiraRestApiUrl + "/search")
                        .queryParam("jql", jql)
                        .queryParam("startAt", currentStartAt)
                        .queryParam("maxResults", maxResults)
                        .encode(StandardCharsets.UTF_8);

                URI uri = uriBuilder.build().toUri();

                ResponseEntity<Map> response = jiraRestTemplate.getForEntity(
                        uri,
                        Map.class
                );
                if (response.getBody() != null) {
                    Map<String, Object> responseBody = response.getBody();

                    for (Map<String, Object> issue : (List<Map<String, Object>>) responseBody.get("issues")) {
                        allIssues.put((String) issue.get("key"), issue);
                    }

                    total = (int) responseBody.get("total");
                    currentStartAt += maxResults;

                    if (limit != null && limit > 0) {
                        if (currentStartAt == limit) {
                            break;
                        }
                        maxResults = currentStartAt + maxResults > limit ? limit : maxResults;
                    }
                } else {
                    break;
                }
            } while (currentStartAt < total);
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }

        return allIssues;
    }

    @Override
    public Map<String, Object> getAllIssues(String projectKey,
                                            int startAt,
                                            int maxResults,
                                            Integer limit) {
        int currentStartAt = startAt;
        int total;
        Map<String, Object> allIssues = new LinkedHashMap<>();

        try {
            do {
                URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + jiraRestApiUrl + "/search")
                        .queryParam("jql", "project=%s".formatted(projectKey))
                        .queryParam("startAt", currentStartAt)
                        .queryParam("maxResults", maxResults)
                        .encode(StandardCharsets.UTF_8)
                        .build()
                        .toUri();

                ResponseEntity<Map> response = jiraRestTemplate.getForEntity(
                        uri,
                        Map.class
                );
                if (response.getBody() != null) {
                    Map<String, Object> responseBody = response.getBody();

                    for (Map<String, Object> issue : (List<Map<String, Object>>) responseBody.get("issues")) {
                        allIssues.put((String) issue.get("key"), issue);
                    }

                    total = (int) responseBody.get("total");
                    currentStartAt += maxResults;

                    if (limit != null && limit > 0) {
                        if (currentStartAt == limit) {
                            break;
                        }
                        maxResults = currentStartAt + maxResults > limit ? limit : maxResults;
                    }
                } else {
                    break;
                }
            } while (currentStartAt < total);
        } catch (HttpClientErrorException e) {
            log.error("Error creating task: {}", e.getResponseBodyAsString());
            throw e;
        }

        return allIssues;
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