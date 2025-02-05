package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.request.create.IssueCreateRequestDto;
import faang.school.projectservice.dto.jira.request.update.IssueUpdateRequestDto;
import faang.school.projectservice.dto.jira.response.IssueCreateResponseDto;
import faang.school.projectservice.dto.jira.response.IssueDto;
import faang.school.projectservice.dto.jira.response.IssueResponseDto;
import faang.school.projectservice.service.jira.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/jira/issues")
public class JiraController {

    private final JiraService jiraService;

    @GetMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public IssueResponseDto getAllIssuesByProject(@PathVariable("projectId") String projectId) {
        return jiraService.getAllIssuesByProject(projectId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IssueDto getIssuesById(@PathVariable("id") String id) {
        return jiraService.getIssueById(id);
    }

    @GetMapping("/assignee/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public IssueResponseDto getIssuesByAssignee(@PathVariable("userId") String userId) {
        return jiraService.getIssuesByAssignee(userId);
    }

    @GetMapping("/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public IssueResponseDto getIssuesByStatus(@PathVariable("status") String status) {
        return jiraService.getIssuesByStatus(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssueCreateResponseDto createIssue(@RequestBody IssueCreateRequestDto dto) {
        return jiraService.createIssue(dto);
    }


    @PutMapping("/{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editIssue(@PathVariable("issueId") String issueId, @RequestBody IssueUpdateRequestDto dto) {
        jiraService.editIssue(issueId, dto);
    }
}
