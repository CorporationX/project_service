package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.exception.jira.JiraApiException;
import faang.school.projectservice.exception.jira.JiraNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class JiraGateway {

    private final RestClient restClient;

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

    public void editIssue(String issueId, IssueUpdateRequestDto requestDto) {
        restClient.put()
                .uri(String.format("/issue/%s", issueId))
                .body(requestDto)
                .retrieve();
    }
}
