package faang.school.projectservice.controller.integrationTests;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

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

@AutoConfigureMockMvc
@Sql(scripts = {"/clear.sql", "/data.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class CampaignControllerIT extends AbstractIntegrationTest {

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

    @Autowired
    public CampaignRepository campaignRepository;

    @Autowired
    private MockMvc mockMvc;

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
