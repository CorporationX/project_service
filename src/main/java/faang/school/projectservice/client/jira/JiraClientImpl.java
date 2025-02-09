package faang.school.projectservice.client.jira;

import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.exception.jira.JiraApiException;
import faang.school.projectservice.exception.jira.JiraNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class JiraClientImpl implements JiraClient {

    private final RestClient restClient;

    @Override
    public IssueResponseDto getAllIssuesByProject(String projectId) {
        return restClient.get()
                .uri(String.format("/search?jql=project=%s", projectId))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (req, resp) -> {
                    throw new JiraNotFoundException(
                            String.format("Client error: %s", resp.getStatusText()));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, (req, resp) -> {
                    throw new JiraApiException(
                            String.format("Internal error: %s", resp.getStatusText()));
                })
                .toEntity(IssueResponseDto.class)
                .getBody();
    }

    @Override
    public IssueDto getIssueById(String issueId) {
        return restClient.get()
                .uri(String.format("/issue/%s", issueId))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (req, resp) -> {
                    throw new JiraNotFoundException(
                            String.format("Client error: %s", resp.getStatusText()));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, (req, resp) -> {
                    throw new JiraApiException(
                            String.format("Internal error: %s", resp.getStatusText()));
                })
                .toEntity(IssueDto.class)
                .getBody();
    }

    @Override
    public IssueResponseDto getIssuesByAssignee(String assigneeId) {
        return restClient.get()
                .uri(String.format("/search?jql=assignee=%s", assigneeId))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (req, resp) -> {
                    throw new JiraNotFoundException(
                            String.format("Client error: %s", resp.getStatusText()));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, (req, resp) -> {
                    throw new JiraApiException(
                            String.format("Internal error: %s", resp.getStatusText()));
                })
                .toEntity(IssueResponseDto.class)
                .getBody();
    }

    @Override
    public IssueResponseDto getIssuesByStatus(String issueStatus) {
        return restClient.get()
                .uri(String.format("/search?jql=status=%s", issueStatus))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (req, resp) -> {
                    throw new JiraNotFoundException(
                            String.format("Client error: %s", resp.getStatusText()));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, (req, resp) -> {
                    throw new JiraApiException(
                            String.format("Internal error: %s", resp.getStatusText()));
                })
                .toEntity(IssueResponseDto.class)
                .getBody();
    }

    @Override
    public IssueCreateResponseDto createIssue(IssueCreateRequestDto requestDto) {
        return restClient.post()
                .uri("/issue")
                .body(requestDto)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (req, resp) -> {
                    throw new JiraNotFoundException(
                            String.format("Client error: %s", resp.getStatusText()));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, (req, resp) -> {
                    throw new JiraApiException(
                            String.format("Internal error: %s", resp.getStatusText()));
                })
                .toEntity(IssueCreateResponseDto.class)
                .getBody();
    }

    @Override
    public void editIssue(String issueId, IssueUpdateRequestDto requestDto) {
        restClient.put()
                .uri(String.format("/issue/%s", issueId))
                .body(requestDto)
                .retrieve();
    }
}
