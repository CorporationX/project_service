package faang.school.projectservice.controller.meet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class MeetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeetRepository meetRepository;

    @Autowired
    private MeetMapper meetMapper;

    @Autowired
    private ProjectRepository projectRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.3").withInitScript("db/user-service/all-migrations.sql");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) throws InterruptedException {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        Thread.sleep(1000);
    }

    @Test
    void testCreateMeetWhenUserNotProjectMember() throws Exception {
        MeetDto meetDto = MeetDto.builder()
                .title("title")
                .description("description")
                .date(LocalDateTime.now())
                .status(MeetStatus.PENDING)
                .build();
        mockMvc.perform(
                post("/api/v1/meets/projects/1")
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto))
        ).andExpect(status().isForbidden());
    }

    @Test
    @Sql("/db/meet/add-project-and-member.sql")
    void testCreateMeetCreated() throws Exception {
        MeetDto meetDto = MeetDto.builder()
                .title("title").description("description")
                .date(LocalDateTime.now())
                .status(MeetStatus.PENDING)
                .build();
        mockMvc.perform(
                post("/api/v1/meets/projects/1")
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetDto))
        ).andExpect(status().isCreated());

        List<Meet> meetsFromDb = meetRepository.findAll();
        assertEquals(1, meetsFromDb.size());
        assertEquals(meetDto.getTitle(), meetsFromDb.get(0).getTitle());
        assertEquals(meetDto.getDescription(), meetsFromDb.get(0).getDescription());
        assertEquals(meetDto.getStatus(), meetsFromDb.get(0).getStatus());
    }

    @Test
    @Sql("/db/meet/add-project-and-member.sql")
    void testUpdateMeetUpdated() throws Exception {
        long userId = 1;
        Project project = projectRepository.findAll().get(0);
        Meet meet = createMeetInDb(userId, project, MeetStatus.PENDING);

        UpdateMeetDto updateMeetDto = new UpdateMeetDto("new title", "new description");
        mockMvc.perform(
                patch("/api/v1/meets/" + meet.getId())
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMeetDto))
        ).andExpect(status().isOk());

        Meet updatedMeet = meetRepository.findById(meet.getId())
                .orElseThrow(() -> new EntityNotFoundException("Meet not found"));
        assertEquals(updateMeetDto.getTitle(), updatedMeet.getTitle());
        assertEquals(updateMeetDto.getDescription(), updatedMeet.getDescription());
    }

    @Test
    @Sql("/db/meet/add-project-and-member.sql")
    void testUpdateMeetWhenNotCreator() throws Exception {
        long userId = 1;
        Project project = projectRepository.findAll().get(0);
        Meet meet = createMeetInDb(2, project, MeetStatus.PENDING);

        UpdateMeetDto updateMeetDto = new UpdateMeetDto("new title", "new description");
        mockMvc.perform(
                patch("/api/v1/meets/" + meet.getId())
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMeetDto))
        ).andExpect(status().isForbidden());
    }

    @Test
    @Sql("/db/meet/add-project-and-member.sql")
    void testChangeMeetStatus() throws Exception {
        long userId = 1;
        Project project = projectRepository.findAll().get(0);
        Meet meet = createMeetInDb(userId, project, MeetStatus.PENDING);
        MeetStatus newStatus = MeetStatus.CANCELLED;

        MvcResult result = mockMvc.perform(
                patch("/api/v1/meets/status/" + meet.getId())
                        .header("x-user-id", userId)
                        .param("status", newStatus.name())
        ).andExpect(status().isOk()).andReturn();

        Meet updatedMeet = meetRepository.findById(meet.getId())
                .orElseThrow(() -> new EntityNotFoundException("Meet not found"));
        assertEquals(newStatus, updatedMeet.getStatus());
        assertEquals(newStatus,
                objectMapper.readValue(result.getResponse().getContentAsString(), MeetDto.class).getStatus());
    }

    @Test
    @Sql("/db/meet/add-project-and-member.sql")
    void testChangeMeetStatusWhenNotCreator() throws Exception {
        long userId = 1;
        Project project = projectRepository.findAll().get(0);
        MeetStatus oldStatus = MeetStatus.PENDING;
        MeetStatus newStatus = MeetStatus.CANCELLED;
        Meet meet = createMeetInDb(2, project, MeetStatus.PENDING);

        mockMvc.perform(
                patch("/api/v1/meets/status/" + meet.getId())
                        .header("x-user-id", userId)
                        .param("status", newStatus.name())
        ).andExpect(status().isForbidden());

        Meet updatedMeet = meetRepository.findById(meet.getId())
                .orElseThrow(() -> new EntityNotFoundException("Meet not found"));
        assertEquals(oldStatus, updatedMeet.getStatus());
    }

    private Meet createMeetInDb(long userId, Project project, MeetStatus status) {
        Meet meet = new Meet();
        meet.setTitle("title");
        meet.setDescription("description");
        meet.setStatus(status);
        meet.setCreatorId(userId);
        meet.setProject(project);
        meet.setDate(LocalDateTime.now());
        meet = meetRepository.save(meet);
        return meet;
    }
}