package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.service.jira.JiraGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/jira/issues")
public class JiraController {

    private final JiraGateway jiraGateway;

    @GetMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public IssueResponseDto getAllIssuesByProject(@PathVariable("projectId") String projectId) {
        return jiraGateway.getAllIssuesByProject(projectId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IssueDto getIssuesById(@PathVariable("id") String id) {
        return jiraGateway.getIssueById(id);
    }

    @GetMapping("/assignee/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public IssueResponseDto getIssuesByAssignee(@PathVariable("userId") String userId) {
        return jiraGateway.getIssuesByAssignee(userId);
    }

    @GetMapping("/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public IssueResponseDto getIssuesByStatus(@PathVariable("status") String status) {
        return jiraGateway.getIssuesByStatus(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssueCreateResponseDto createIssue(@RequestBody IssueCreateRequestDto dto) {
        return jiraGateway.createIssue(dto);
    }

    @PutMapping("/{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editIssue(@PathVariable("issueId") String issueId, @RequestBody IssueUpdateRequestDto dto) {
        jiraGateway.editIssue(issueId, dto);
    }
}
