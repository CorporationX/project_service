package faang.school.projectservice.service.jira;


import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.mapper.jira.IssueMapper;
import faang.school.projectservice.model.jira.Issue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class JiraService {

    private static final String JIRA_REST_API_ISSUE = "/rest/api/3/issue";

    @Value("${services.jira.url}")
    private String jiraUrl;
    @Value("${services.jira.username}")
    private String jiraUsername;
    @Value("${services.jira.api-token}")
    private String jiraApiToken;

    private final RestTemplate restTemplate;

    private final IssueMapper issueMapper;

    public ResponseEntity<String> createIssue(IssueDto issueDto) {
        Issue issue = issueMapper.toEntity(issueDto);
        String jsonString = issue.toPrettyJsonString();
        HttpEntity<String> entity = new HttpEntity<>(jsonString, getHttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(jiraUrl + JIRA_REST_API_ISSUE, HttpMethod.POST, entity, String.class);
        return response;
    }


    public ResponseEntity<String> getIssue(String issueIdOrKey) {
        String url = jiraUrl + "/rest/api/3/issue/" + issueIdOrKey;
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response;
    }

    public ResponseEntity<String> getAllIssue() {
        String url = jiraUrl + "/rest/api/3/search/";
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response;
    }


    public ResponseEntity<String> getIssuesByFilter(IssueFilterDto issueFilterDto) {
        issueFilterDto.setAssignee("712020:2ade8460-0fb7-4173-b703-be4ea0eef049");
        String url = jiraUrl + "/rest/api/3/search?jql=project=\""
                + issueFilterDto.getProjectKey() +
                "\" AND status=\"" + issueFilterDto.getStatus() +
                "\" AND assignee=\"" + issueFilterDto.getAssignee() + "\"";
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response;
    }


    private HttpHeaders getHttpHeaders() {
        String auth = Base64.getEncoder().encodeToString((jiraUsername + ":" + jiraApiToken).getBytes(StandardCharsets.UTF_8));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Basic " + auth);
        return httpHeaders;
    }


}
