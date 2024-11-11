package faang.school.projectservice.controller.campaign;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignPublishingDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class CampaignControllerMockMvcIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TeamRepository teamRepository;

    private static final long ID = 1L;
    private static final long WRONG_ID = 2L;
    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String NEW_TITLE = "newTitle";
    private static final String DESCRIPTION = "description";
    private static final BigDecimal GOAL = BigDecimal.valueOf(100);
    private static final BigDecimal AMOUNT_RAISED = BigDecimal.valueOf(200);
    private CampaignPublishingDto campaignPublishingDto;
    private CampaignUpdateDto campaignUpdateDto;
    private Campaign campaign;
    private Project project;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:14")
                    .withInitScript("db/create_table_users_test.sql");

    @DynamicPropertySource
    static void start(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Nested
    class PositiveTests {

        @BeforeEach
        public void init() {
            project = Project.builder()
                    .id(ID)
                    .name(NAME)
                    .status(ProjectStatus.CREATED)
                    .visibility(ProjectVisibility.PUBLIC)
                    .build();
            projectRepository.save(project);
            campaign = Campaign.builder()
                    .id(ID)
                    .title(TITLE)
                    .description(DESCRIPTION)
                    .status(CampaignStatus.ACTIVE)
                    .project(project)
                    .build();
            campaignRepository.save(campaign);
            campaignPublishingDto = CampaignPublishingDto.builder()
                    .title(TITLE)
                    .description(DESCRIPTION)
                    .projectId(ID)
                    .build();
            campaignUpdateDto = CampaignUpdateDto.builder()
                    .status(CampaignStatus.COMPLETED)
                    .title(NEW_TITLE)
                    .goal(GOAL)
                    .amountRaised(AMOUNT_RAISED)
                    .build();
        }

        @Test
        @DisplayName("Success when delete campaign by id")
        public void whenDeleteCampaignByIdThenSuccess() throws Exception {
            mockMvc.perform(delete("/v1/campaigns/{campaignId}", ID))
                    .andExpect(status().isNoContent());

            Optional<Campaign> result = campaignRepository.findById(ID);
            assertTrue(result.isPresent());
            assertEquals(CampaignStatus.CANCELED, result.get().getStatus());
        }

        @Test
        @DisplayName("Success when get campaign by id")
        public void whenGetCampaignByIdThenReturnCampaignDto() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/v1/campaigns/{campaignId}", ID))
                    .andExpect(status().isOk())
                    .andReturn();
            CampaignDto result = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(),
                    CampaignDto.class);

            assertNotNull(result);
            assertEquals(ID, result.getId());
        }

        @Test
        @DisplayName("Success get all campaigns by project id with empty CampaignFilterDto")
        public void whenGetAllCampaignsByProjectIdWithEmptyCampaignFilterDtoThenSuccess() throws Exception {
            mockMvc.perform(get("/v1/campaigns/project/{projectId}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(CampaignFilterDto.builder().build())))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Success get all campaigns by project id with CampaignFilterDto")
        public void whenGetAllCampaignsByProjectIdWithCampaignFilterDtoThenSuccess() throws Exception {
            mockMvc.perform(get("/v1/campaigns/project/{projectId}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(CampaignFilterDto.builder()
                                    .status(CampaignStatus.ACTIVE)
                                    .build())))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    class NegativeTests {

        @BeforeEach
        public void init() {
            project = Project.builder()
                    .id(ID)
                    .name(NAME)
                    .status(ProjectStatus.CREATED)
                    .visibility(ProjectVisibility.PUBLIC)
                    .build();
            projectRepository.save(project);
            campaign = Campaign.builder()
                    .id(ID)
                    .title(TITLE)
                    .description(DESCRIPTION)
                    .status(CampaignStatus.ACTIVE)
                    .project(project)
                    .build();
            campaignRepository.save(campaign);
            campaignPublishingDto = CampaignPublishingDto.builder()
                    .title(TITLE)
                    .description(DESCRIPTION)
                    .projectId(ID)
                    .build();
            campaignUpdateDto = CampaignUpdateDto.builder()
                    .title(TITLE)
                    .description(DESCRIPTION)
                    .goal(GOAL)
                    .amountRaised(AMOUNT_RAISED)
                    .status(CampaignStatus.COMPLETED)
                    .build();
        }

        @Test
        @DisplayName("When publishing campaign by user with wrong id then throw exception")
        public void whenPublishingCampaignWithWrongUserIdThenThrowException() throws Exception {
            mockMvc.perform(post("/v1/campaigns")
                            .header("x-user-id", WRONG_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(campaignPublishingDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("When publishing campaign with invalid CampaignPublishingDto then throw exception")
        public void whenPublishingCampaignWithInvalidCampaignPublishingDtoThenThrowException() throws Exception {
            mockMvc.perform(post("/v1/campaigns")
                            .header("x-user-id", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(CampaignPublishingDto.builder().build())))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("When update campaign with wrong id then throw exception")
        public void whenUpdateCampaignWithWrongIdThenThrowException() throws Exception {
            mockMvc.perform(put("/v1/campaigns/{campaignId}", WRONG_ID)
                            .header("x-user-id", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(campaignUpdateDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("When delete campaign by id that does not exist then throw exception")
        public void whenDeleteCampaignByIdWithIdDoesNotExistThenThrowException() throws Exception {
            mockMvc.perform(delete("/v1/campaigns/{campaignId}", WRONG_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("When get campaign by id that does not exist then throw exception")
        public void whenGetCampaignByIdWithIdDoesNotExistThenThrowException() throws Exception {
            mockMvc.perform(get("/v1/campaigns/{campaignId}", WRONG_ID))
                    .andExpect(status().isNotFound());
        }
//
//        @Test
//        @DisplayName("When get all campaigns by project id with wrong id then throw exception")
//        public void whenGetAllCampaignsByProjectIdWithWrongIdThenThrowException() throws Exception {
//            mockMvc.perform(get("/v1/campaigns/project/{projectId}", WRONG_ID)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .characterEncoding(StandardCharsets.UTF_8)
//                            .content(objectMapper.writeValueAsString(CampaignFilterDto.builder().build())))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$", hasSize(0)));
//        }
    }
}
