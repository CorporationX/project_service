package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ChangeTaskStatusDto;
import faang.school.projectservice.dto.redis.TaskCompletedEvent;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.testConfig.TestBeansConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestBeansConfig.class)
public class ProjectControllerTest {
    @Autowired
    private GenericContainer<?> redisContainer;
    @Autowired
    private PostgreSQLContainer<?> postgresContainer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProjectJpaRepository projectRepository;

    @BeforeEach
    void setUp() {
        redisContainer.start();
        postgresContainer.start();
        projectRepository.deleteAll();
        System.setProperty("spring.redis.host", redisContainer.getHost());
        System.setProperty("spring.redis.port", redisContainer.getMappedPort(6379).toString());
    }

    @AfterEach
    void close() {
        redisContainer.stop();
        postgresContainer.stop();
    }

    @Test
    @Transactional
    void changeTaskStatus() throws Exception {
        Task task = Task.builder().id(1L).name("task").status(TaskStatus.IN_PROGRESS).performerUserId(1L).build();
        Project project = Project.builder().name("project").status(ProjectStatus.IN_PROGRESS).visibility(ProjectVisibility.PUBLIC).tasks(List.of(task)).build();
        projectRepository.save(project);
        project = projectRepository.findAll().get(0);

        ChangeTaskStatusDto changeTaskStatusDto = new ChangeTaskStatusDto();
        changeTaskStatusDto.setProjectId(project.getId());
        changeTaskStatusDto.setTaskStatus(TaskStatus.DONE);
        changeTaskStatusDto.setTaskId(1L);

        TaskCompletedEvent taskCompletedEvent = new TaskCompletedEvent();
        taskCompletedEvent.setProjectId(project.getId());
        taskCompletedEvent.setTaskId(1L);
        taskCompletedEvent.setUserId(1L);

        mockMvc.perform(put("/api/v1/projects/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1)
                        .content(objectMapper.writeValueAsString(changeTaskStatusDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(taskCompletedEvent)));
    }
}
