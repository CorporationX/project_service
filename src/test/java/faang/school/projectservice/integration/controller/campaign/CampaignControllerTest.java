package faang.school.projectservice.integration.controller.campaign;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.campaign.CampaignController;
import faang.school.projectservice.integration.IntegrationTestBase;
import faang.school.projectservice.model.dto.campaign.CampaignDto;
import faang.school.projectservice.model.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.dto.client.Currency;
import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.model.entity.CampaignStatus;
import faang.school.projectservice.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class CampaignControllerTest extends IntegrationTestBase {

    @Autowired
    private CampaignController campaignController;
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Autowired
    private CampaignRepository campaignRepository;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testPublishCampaign_OK() throws Exception {
        CampaignDto campaignDto = CampaignDto.builder()
                .projectId(1L)
                .title("Title")
                .description("desc")
                .amountRaised(BigDecimal.ZERO)
                .status(CampaignStatus.ACTIVE)
                .currency(Currency.USD)
                .build();
        String jsonCampaign = objectMapper.writeValueAsString(campaignDto);

        mockMvc.perform(post("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCampaign)
                        .header("x-user-id", 1))
                .andExpectAll(status().isCreated(),
                        jsonPath("$.title").value("Title"),
                        jsonPath("$.status").value(CampaignStatus.ACTIVE.name()));

        List<Campaign> campaigns = campaignRepository.findAll();
        assertEquals(2, campaigns.size());
        assertEquals(campaignDto.title(), campaigns.get(1).getTitle());
        assertEquals(campaignDto.description(), campaigns.get(1).getDescription());
    }

    @Test
    void testUpdateCampaign_OK() throws Exception {
        CampaignUpdateDto dto = CampaignUpdateDto.builder()
                .id(1L)
                .title("habibi")
                .build();
        String jsonCampaign = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCampaign)
                        .header("x-user-id", 1))
                .andExpectAll(status().isOk(),
                        jsonPath("$.title").value("habibi"),
                        jsonPath("$.description").value("descr"),
                        jsonPath("$.status").value(CampaignStatus.ACTIVE.name()));

        Campaign updatedCampaign = campaignRepository.findById(1L).get();
        assertEquals(1, updatedCampaign.getId());
        assertEquals("habibi", updatedCampaign.getTitle());
    }

    @Test
    void testGetCampaign_OK() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpectAll(status().isOk(),
                        jsonPath("$.title").value("title"),
                        jsonPath("$.description").value("descr"),
                        jsonPath("$.status").value(CampaignStatus.ACTIVE.name()));
    }

    @Test
    void testDeleteCampaign_OK() throws Exception {
        mockMvc.perform(put("/api/v1/campaigns/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1))
                .andExpect(status().isOk());

        Campaign deletedCampaign = campaignRepository.findById(1L).get();
        assertTrue(deletedCampaign.getRemoved());
    }

    @Test
    void testGetAllCampaignsOfProject_NothingReturned() throws Exception {
        CampaignFilterDto filter = CampaignFilterDto.builder()
                .status(CampaignStatus.COMPLETED)
                .build();
        String jsonFilter = objectMapper.writeValueAsString(filter);
        mockMvc.perform(get("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1)
                        .content(jsonFilter))
                .andExpectAll(status().isOk(),
                        jsonPath("$").isEmpty());
    }

    @Test
    void testGetAllCampaignsOfProject_FoundCampaigns() throws Exception {
        CampaignFilterDto filter = CampaignFilterDto.builder()
                .status(CampaignStatus.ACTIVE)
                .build();
        String jsonFilter = objectMapper.writeValueAsString(filter);
        mockMvc.perform(get("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", 1)
                        .content(jsonFilter))
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].title").value("title"),
                        jsonPath("$[0].description").value("descr"),
                        jsonPath("$[0].status").value(CampaignStatus.ACTIVE.name()));
    }
}
