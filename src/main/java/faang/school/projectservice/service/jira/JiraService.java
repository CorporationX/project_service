package faang.school.projectservice.service.jira;

import faang.school.projectservice.dto.jira.filter.IssueFilterDto;
import faang.school.projectservice.dto.jira.request.IssueRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.dto.jira.response.ProjectResponseDto;
import faang.school.projectservice.dto.jira.update.IssueUpdateDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface JiraService {
    Mono<IssueCreateResponseDto> createIssue(IssueRequestDto issueRequestDto);

    Mono<Void> updateIssue(String key, IssueUpdateDto issueUpdateDto);

    Flux<IssueResponseDto> getAllIssuesWithFilter(Long projectId, IssueFilterDto issueFilterDto);

    Flux<IssueResponseDto> getAllIssuesByProject(Long projectId);

    Mono<IssueResponseDto> getIssueByKey(String key);

    Mono<ProjectResponseDto> registerProject(Long id, String key);
}
