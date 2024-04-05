package faang.school.projectservice.controller.jira;

import faang.school.projectservice.service.jira.JiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jira")
public class JiraController {
    private final JiraService jiraService;

    @PostMapping("/issue")
    public void createIssue () {

    }
}
