package faang.school.projectservice.client;

import faang.school.projectservice.exception.JiraRequestException;
import faang.school.projectservice.model.jira.JiraProject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RequiredArgsConstructor
@Component
public class JiraClient {

    private final RestTemplate restTemplate;

    public String getIssue(JiraProject project, String issueKey) {
        String url = project.getUrl() + "issue/" + issueKey;
        ResponseEntity<String> responseEntity = exchange(url, project, HttpMethod.GET, null);

        return responseEntity.getBody();
    }

    public String getIssuesWithFilter(JiraProject project, String filter) {
        String url = project.getUrl() + "search?jql=" + filter;
        ResponseEntity<String> responseEntity = exchange(url, project, HttpMethod.GET, null);

        return responseEntity.getBody();
    }

    public String createIssue(JiraProject project, String body) {
        String url = project.getUrl() + "issue";
        ResponseEntity<String> responseEntity = exchange(url, project, HttpMethod.POST, body);

        return responseEntity.getBody();
    }

    public String updateIssue(JiraProject project, String issueKey, String body) {
        String url = project.getUrl() + "issue/" + issueKey;
        exchange(url, project, HttpMethod.PUT, body);

        return "Issue fields updated successfully";
    }

    public String changeIssueStatus(JiraProject project, String issueKey, String body) {
        String url = project.getUrl() + "issue/" + issueKey + "/transitions";
        exchange(url, project, HttpMethod.POST, body);

        return "Issue status changed successfully";
    }

    public String createIssueLink(JiraProject project, String body) {
        String url = project.getUrl() + "issueLink";
        exchange(url, project, HttpMethod.POST, body);

        return "Issue link created successfully";
    }

    private ResponseEntity<String> exchange(String url, JiraProject project, HttpMethod method, String body) {
        HttpEntity<String> requestEntity = new HttpEntity<>(body, getHttpHeaders(project));

        try {
            return restTemplate.exchange(
                    url,
                    method,
                    requestEntity,
                    String.class,
                    body
            );
        } catch (HttpClientErrorException e) {
            throw new JiraRequestException(e.getMessage());
        }
    }

    private HttpHeaders getHttpHeaders(JiraProject project) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(project.getUsername(), project.getPassword());
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
