package faang.school.projectservice.integration.controller.campaign;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.campaign.CampaignController;
import faang.school.projectservice.integration.IntegrationTestBase;
import faang.school.projectservice.model.dto.campaign.CampaignDto;
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
        assertEquals(1, campaigns.size());
        assertEquals(campaignDto.title(), campaigns.get(0).getTitle());
        assertEquals(campaignDto.description(), campaigns.get(0).getDescription());
    }
}
