package faang.school.projectservice.controller.integrationTests;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.repository.CampaignRepository;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@ActiveProfiles("it")
@AutoConfigureMockMvc
@Sql(scripts = {"/clear.sql", "/data.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class CampaignControllerIT {

    CampaignDto CampaignRequestDto = CampaignDto.builder()
            .title("title4")
            .description("description4")
            .goal(BigDecimal.valueOf(4))
            .status(CampaignStatus.ACTIVE)
            .projectId(1L)
            .currency(Currency.USD)
            .build();

     CampaignDto updatedRequestDto = CampaignDto.builder()
             .title("updateTitle")
             .description("updateDescription")
             .goal(BigDecimal.valueOf(3))
             .status(CampaignStatus.CANCELED)
             .projectId(1L)
             .currency(Currency.USD)
             .build();

     CampaignFilterDto filterDto = CampaignFilterDto.builder()
             .status(CampaignStatus.COMPLETED)
             .build();

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String URL = "/api/v1/campaigns";

    private static final String MINIO_DOCKER_IMAGE = "minio/minio:latest";
    private static final int MINIO_EXPOSED_PORT = 9000;
    private static final String MINIO_USER = "user";
    private static final String MINIO_PASSWORD = "password";

    @Autowired
    public MinioClient minioClient;

    @Autowired
    public CampaignRepository campaignRepository;

    @Autowired
    private MockMvc mockMvc;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test");

    @Container
    public static final GenericContainer<?> MINIO_CONTAINER
            = new GenericContainer<>(DockerImageName.parse(MINIO_DOCKER_IMAGE))
            .withExposedPorts(MINIO_EXPOSED_PORT)
            .withEnv("MINIO_ROOT_USER", MINIO_USER)
            .withEnv("MINIO_ROOT_PASSWORD", MINIO_PASSWORD)
            .withCommand("server", "/data")
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(MINIO_EXPOSED_PORT),
                            new ExposedPort(MINIO_EXPOSED_PORT)))));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("MINIO_PORT", MINIO_CONTAINER::getFirstMappedPort);
        registry.add("MINIO_ACCESS_KEY", () -> MINIO_USER);
        registry.add("MINIO_SECRET_KEY", () -> MINIO_PASSWORD);
    }

    @Test
    void contextLoad() {
    }

//    @BeforeEach
//    void clearDb() {
//        campaignRepository.deleteAll();
//    }

    @Test
    void createCampaignTest() throws Exception {
        String jsonRequestDto = OBJECT_MAPPER.writeValueAsString(CampaignRequestDto);

        mockMvc.perform(post("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestDto)
                        .header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("title4")))
                .andExpect(jsonPath("$.description", is("description4")))
                .andExpect(jsonPath("$.goal", is(4)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.projectId", is(1)))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.createdBy", is(1)));

        assertTrue(campaignRepository.existsById(4L));


        Campaign savedCampaign = campaignRepository.findById(1L).get();
        assertEquals("title4", savedCampaign.getTitle());
        assertEquals("description4", savedCampaign.getDescription());
        assertEquals(new BigDecimal("4.00"), savedCampaign.getGoal());
        assertEquals(CampaignStatus.ACTIVE, savedCampaign.getStatus());
        assertEquals(1, savedCampaign.getProject().getId());
        assertEquals(Currency.USD, savedCampaign.getCurrency());
        assertEquals(1, savedCampaign.getCreatedBy());
    }

    @Test
    void updateCampaignTest() throws Exception {
        String jsonRequestDto = OBJECT_MAPPER.writeValueAsString(updatedRequestDto);

        mockMvc.perform(put("/api/v1/campaigns/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestDto)
                        .header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("updateTitle")))
                .andExpect(jsonPath("$.description", is("updateDescription")))
                .andExpect(jsonPath("$.goal", is(3)))
                .andExpect(jsonPath("$.status", is("CANCELED")))
                .andExpect(jsonPath("$.projectId", is(1)))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.createdBy", is(1)))
                .andExpect(jsonPath("$.updatedBy", is(1)));

        assertTrue(campaignRepository.existsById(2L));

        Campaign savedCampaign = campaignRepository.findById(2L).get();
        assertEquals("updateTitle", savedCampaign.getTitle());
        assertEquals("updateDescription", savedCampaign.getDescription());
        assertEquals(new BigDecimal("3.00"), savedCampaign.getGoal());
        assertEquals(CampaignStatus.CANCELED, savedCampaign.getStatus());
        assertEquals(1, savedCampaign.getProject().getId());
        assertEquals(Currency.USD, savedCampaign.getCurrency());
        assertEquals(1, savedCampaign.getCreatedBy());
        assertEquals(1, savedCampaign.getUpdatedBy());
    }

    @Test
    void getCampaignTest() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.goal", is(1.0)))
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.projectId", is(1)))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.createdBy", is(1)))
                .andExpect(jsonPath("$.updatedBy", is(1)));
    }

    @Test
    void deleteCampaignTest() throws Exception {
        mockMvc.perform(delete("/api/v1/campaigns/1")
                .header("x-user-id", 1));
        assertFalse(campaignRepository.existsById(1L));
    }

    @Test
    void getCampaignsTest() throws Exception {
        String jsonFilterDto = OBJECT_MAPPER.writeValueAsString(filterDto);

        mockMvc.perform(get("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFilterDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].title", is("title")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].goal", is(1.0)))
                .andExpect(jsonPath("$[0].status", is("COMPLETED")))
                .andExpect(jsonPath("$[0].projectId", is(1)))
                .andExpect(jsonPath("$[0].currency", is("USD")))
                .andExpect(jsonPath("$[0].createdBy", is(1)))
                .andExpect(jsonPath("$[0].updatedBy", is(1)));
    }
}
