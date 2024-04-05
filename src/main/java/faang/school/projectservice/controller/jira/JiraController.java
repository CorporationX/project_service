package faang.school.projectservice.controller.jira;

import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.service.jira.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jira")
public class JiraController {
    private final JiraService jiraService;

    @PostMapping("/issue")
    public String createIssue(@RequestBody IssueDto issueDto) {
        return jiraService.createIssue(issueDto);
    }
}
