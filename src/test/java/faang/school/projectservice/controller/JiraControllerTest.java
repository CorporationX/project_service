package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.jira.IssueDto;
import faang.school.projectservice.dto.jira.IssueFilterDto;
import faang.school.projectservice.dto.jira.IssueTypeDto;
import faang.school.projectservice.service.jira.JiraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class JiraControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JiraService jiraService;

    @InjectMocks
    private JiraController jiraController;

    private final String key = "key";
    private IssueDto issueDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        issueDto = IssueDto.builder()
                .key(key)
                .projectKey(key + "_p")
                .issueType(new IssueTypeDto())
                .summary("summary")
                .description("description")
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now())
                .build();

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(jiraController).build();
    }

    @Test
    void createIssue() throws Exception {
        String json = objectMapper.writeValueAsString(issueDto);

        when(jiraService.createIssue(any(IssueDto.class))).thenReturn(key);

        mockMvc.perform(post("/jira/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string(key));

        InOrder inOrder = inOrder(jiraService);
        inOrder.verify(jiraService).createIssue(any(IssueDto.class));
    }

    @Test
    void getIssue() throws Exception {
        String json = objectMapper.writeValueAsString(issueDto);

        when(jiraService.getIssue(key)).thenReturn(issueDto);

        mockMvc.perform(get("/jira/issues/" + key))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        InOrder inOrder = inOrder(jiraService);
        inOrder.verify(jiraService).getIssue(key);
    }

    @Test
    void getAllIssues() throws Exception {
        String json = objectMapper.writeValueAsString(List.of(issueDto));

        when(jiraService.getAllIssues(key)).thenReturn(List.of(issueDto));

        mockMvc.perform(get("/jira/" + key + "/issues"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        InOrder inOrder = inOrder(jiraService);
        inOrder.verify(jiraService).getAllIssues(key);
    }

    @Test
    void getIssuesByFilter() throws Exception {
        String json = objectMapper.writeValueAsString(List.of(issueDto));

        when(jiraService.getIssuesByFilter(eq(key), any(IssueFilterDto.class))).thenReturn(List.of(issueDto));

        mockMvc.perform(get("/jira/" + key + "/issues/filter?statusId=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        InOrder inOrder = inOrder(jiraService);
        inOrder.verify(jiraService).getIssuesByFilter(eq(key), any(IssueFilterDto.class));
    }
}