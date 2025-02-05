package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final RestClient restClient;

    @Override
    public IssueResponseDto getAllIssuesByProject(String projectId) {
        return restClient.get()
                .uri(String.format("/search?jql=project=%s", projectId))
                .retrieve()
                .toEntity(IssueResponseDto.class)
                .getBody();
    }

    @Override
    public IssueDto getIssueById(String issueId) {
        return restClient.get()
                .uri(String.format("/issue/%s", issueId))
                .retrieve()
                .toEntity(IssueDto.class)
                .getBody();
    }

    @Override
    public IssueResponseDto getIssuesByAssignee(String assigneeId) {
        return restClient.get()
                .uri(String.format("/search?jql=assignee=%s", assigneeId))
                .retrieve()
                .toEntity(IssueResponseDto.class)
                .getBody();
    }

    @Override
    public IssueResponseDto getIssuesByStatus(String issueStatus) {
        return restClient.get()
                .uri(String.format("/search?jql=status=%s", issueStatus))
                .retrieve()
                .toEntity(IssueResponseDto.class)
                .getBody();
    }

    @Override
    public IssueCreateResponseDto createIssue(IssueCreateRequestDto requestDto) {
        return restClient.post()
                .uri("/issue")
                .body(requestDto)
                .retrieve()
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
