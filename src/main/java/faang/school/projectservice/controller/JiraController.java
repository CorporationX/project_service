package faang.school.projectservice.controller;

//import faang.school.projectservice.dto.issue.IssueDto;
//import faang.school.projectservice.service.JiraService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/jira")
//@RequiredArgsConstructor
//public class JiraController {
//    private final JiraService jiraService;
//
//    @PostMapping("/issue/{projectKey}")
//    @ResponseStatus(HttpStatus.OK)
//    public void createIssue(
//            @PathVariable String projectKey, @RequestBody IssueDto issueDto) {
//        jiraService.createIssue(projectKey, issueDto);
//    }
//
//    @PutMapping("/issue/{issueKey}/description")
//    @ResponseStatus(HttpStatus.OK)
//    public void updateIssueDescription(
//            @PathVariable String issueKey, @RequestParam String description) {
//        jiraService.updateIssueDescription(issueKey, description);
//    }
//
//    @PutMapping("/issue/{issueKey}/status")
//    @ResponseStatus(HttpStatus.OK)
//    public void updateIssueStatus(
//            @PathVariable String issueKey, @RequestParam int statusId) {
//        jiraService.updateIssueStatus(issueKey, statusId);
//    }
//
//    @PutMapping("/issue/{issueKey}/parent")
//    @ResponseStatus(HttpStatus.OK)
//    public void updateIssueParent
//            (@PathVariable String issueKey, @RequestParam String parentKey) {
//        jiraService.updateIssueParent(issueKey, parentKey);
//    }
//
//    @GetMapping("/issue/{issueKey}")
//    public IssueDto getIssueDto(@PathVariable String issueKey) {
//        return jiraService.getIssueDto(issueKey);
//    }
//
//    @DeleteMapping("/issue/{issueKey}")
//    @ResponseStatus(HttpStatus.OK)
//    public void deleteIssue(
//            @PathVariable String issueKey, @RequestParam boolean deleteSubtask) {
//        jiraService.deleteIssue(issueKey, deleteSubtask);
//    }
//
//    @GetMapping("/issue")
//    public Iterable<IssueDto> searchIssues(@RequestParam String jql) {
//        return jiraService.searchIssues(jql);
//    }
//
//    @PatchMapping("/issue/{issueKey}/comment")
//    @ResponseStatus(HttpStatus.OK)
//    public void addComment(@PathVariable String issueKey, @RequestParam String comment) {
//        jiraService.addComment(issueKey, comment);
//    }
//}