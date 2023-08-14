package faang.school.projectservice.controller;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectControllerTest {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private MockMvc mockMvc;
    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Project project = Project.builder()
            .name("project1")
            .ownerId(1L)
            .description("description")
            .status(ProjectStatus.CREATED)
            .visibility(ProjectVisibility.PUBLIC)
            .build();

    @Test
    public void getProjectByIdExistTest() throws Exception {
        projectRepository.save(project);
        mockMvc.perform(get("/project/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getProjectByIdNotExistTest() throws Exception {
        mockMvc.perform(get("/project/12"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllProjectsTest() throws Exception {
        mockMvc.perform(get("/project/all"))
                .andExpect(status().isOk());
    }

    @Test
    public void createProjectTest() throws Exception {
        mockMvc.perform(
                post("/project")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "project",
                                  "description": "description",
                                  "ownerId": 1,
                                  "status": "CREATED",
                                  "visibility": "PUBLIC"
                                }""")
        ).andExpect(status().isOk());
    }

    @Test
    public void createProjectWithNameAlreadyExistTest() throws Exception {
        projectRepository.save(project);
        mockMvc.perform(
                post("/project")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "project1",
                                  "description": "description",
                                  "ownerId": 1,
                                  "status": "CREATED",
                                  "visibility": "PUBLIC"
                                }""")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateProjectTest() throws Exception {
        projectRepository.save(project);

        ResultActions resultActions = mockMvc.perform(
                put("/project/1")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "project1",
                                  "description": "description",
                                  "ownerId": 1,
                                  "status": "CREATED",
                                  "visibility": "PRIVATE"
                                }""")
        ).andExpect(status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        Project actual = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), Project.class);
        assertEquals(ProjectVisibility.PRIVATE, actual.getVisibility());
    }

    @Test
    public void getProjectByFilterTest() throws Exception {
        projectRepository.save(project);
        projectRepository.save(Project.builder()
                .id(2L)
                .name("project2")
                .ownerId(1L)
                .status(ProjectStatus.ON_HOLD)
                .visibility(ProjectVisibility.PUBLIC)
                .build());

        ResultActions resultActions = mockMvc.perform(
                get("/project/list")
                        .contentType("application/json")
                        .content("""
                                {
                                "status": "CREATED"
                                }""")
        ).andExpect(status().isOk());

        ObjectMapper objectMapper = new ObjectMapper();
        List<Project> actual = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(),
                objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, Project.class));

        assertEquals("project1", actual.get(0).getName());

        assertEquals(1, actual.size());
    }
}