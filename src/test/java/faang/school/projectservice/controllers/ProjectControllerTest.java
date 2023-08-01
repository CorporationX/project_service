package faang.school.projectservice.controllers;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;



@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    Project project;
    ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
        project = Project.builder()
                .id(1L)
                .name("Project1")
                .description("Description1")
                .ownerId(1L)
                .visibility(ProjectVisibility.valueOf("PUBLIC"))
                .build();
        projectDto = ProjectDto.builder()
                .name("Project1")
                .description("Description1")
                .ownerId(1L)
                .visibility("PUBLIC")
                .build();

    }

//    @Test
//    void testCreateProject() throws Exception {
//        when(projectService.createProject(projectDto))).thenReturn(projectDto);
//        mockMvc.perform(post("/projects"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is("Project1")))
//                .andExpect(jsonPath("$.description", is("Description1")))
//                .andExpect(jsonPath("$.ownerId", is(1)))
//                .andExpect(jsonPath("$.visibility", is("PUBLIC")));
//    }

    @Test
    void testUpdateProject() {

    }

    @Test
    void testGetProjectById() {

    }

    @Test
    void testGetAllProject() {
    }
}