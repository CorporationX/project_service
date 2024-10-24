package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.CampaignDto;
import faang.school.projectservice.model.dto.CampaignFilterDto;
import faang.school.projectservice.model.enums.Currency;
import faang.school.projectservice.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CampaignControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController campaignController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(campaignController).build();
    }

    @Test
    void createCampaignTest() throws Exception {
        CampaignDto campaignDto = new CampaignDto();
        campaignDto.setTitle("New Campaign");
        campaignDto.setGoal(new BigDecimal("10000"));
        campaignDto.setCurrency(Currency.USD);
        campaignDto.setProjectId(1L);
        campaignDto.setCreatedBy(1L);

        given(campaignService.create(any(CampaignDto.class))).willReturn(campaignDto);

        mockMvc.perform(post("/campaign")
                .header("x-user-id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"New Campaign\",\"goal\":10000,\"currency\":\"USD\",\"projectId\":1,\"createdBy\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Campaign"));

        verify(campaignService, times(1)).create(any(CampaignDto.class));
    }

    @Test
    void updateCampaignTest() throws Exception {
        CampaignDto campaignDto = new CampaignDto();
        campaignDto.setId(1L);
        campaignDto.setTitle("Updated Campaign");
        campaignDto.setGoal(new BigDecimal("20000"));
        campaignDto.setCurrency(Currency.EUR);
        campaignDto.setProjectId(1L);
        campaignDto.setUpdatedBy(1L);

        given(campaignService.update(eq(1L), any(CampaignDto.class))).willReturn(campaignDto);

        mockMvc.perform(put("/campaign/{id}", 1)
                .header("x-user-id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Campaign\",\"goal\":20000,\"currency\":\"EUR\",\"projectId\":1,\"createdBy\":1}"))
                .andExpect(status().isOk());

        verify(campaignService, times(1)).update(eq(1L), any(CampaignDto.class));
    }

    @Test
    void deleteCampaignTest() throws Exception {
        doNothing().when(campaignService).delete(1L);

        mockMvc.perform(delete("/campaign/{id}", 1)
                .header("x-user-id", "1"))
                .andExpect(status().isOk());

        verify(campaignService, times(1)).delete(1L);
    }

    @Test
    void getCampaignByIdTest() throws Exception {
        CampaignDto campaignDto = new CampaignDto();
        campaignDto.setId(1L);
        campaignDto.setTitle("Specific Campaign");

        given(campaignService.getById(1L)).willReturn(campaignDto);

        mockMvc.perform(get("/campaign/{id}", 1)
                .header("x-user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Specific Campaign"));

        verify(campaignService, times(1)).getById(1L);
    }

    @Test
    void getCampaignsByFilterTest() throws Exception {
        CampaignFilterDto filterDto = new CampaignFilterDto();
        List<CampaignDto> campaigns = List.of(new CampaignDto(), new CampaignDto());

        given(campaignService.getByFilter(any(CampaignFilterDto.class))).willReturn(campaigns);

        mockMvc.perform(post("/campaign/filter")
                .header("x-user-id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(campaignService, times(1)).getByFilter(any(CampaignFilterDto.class));
    }
}
