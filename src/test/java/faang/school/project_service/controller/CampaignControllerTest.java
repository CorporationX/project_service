package faang.school.project_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.controller.campaign.CampaignController;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.service.campaign.CampaignService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CampaignControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String basePath = "/campaigns";
    private MockMvc mockMvc;

    @Mock
    private CampaignService campaignService;
    @InjectMocks
    private CampaignController campaignController;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(campaignController).build();
    }

    @Test
    void testCreate() throws Exception {
        CreateCampaignDto createCampaignDto = CreateCampaignDto.builder()
                .title("title")
                .description("desc")
                .goal(new BigDecimal("100.00"))
                .projectId(23L)
                .currency(Currency.EUR)
                .build();

        CampaignDto campaignDto = CampaignDto
                .builder()
                .title(createCampaignDto.title())
                .build();

        when(campaignService.create(createCampaignDto)).thenReturn(campaignDto);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCampaignDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(CampaignDto.Fields.title, Matchers.equalTo(campaignDto.title())));
    }

    @Test
    void testUpdate() throws Exception {
        long id = 1L;

        UpdateCampaignDto updateCampaignDto = UpdateCampaignDto.builder()
                .status(CampaignStatus.ACTIVE)
                .title("new title")
                .build();

        CampaignDto campaignDto = CampaignDto.builder()
                .status(updateCampaignDto.status())
                .title(updateCampaignDto.title())
                .build();

        when(campaignService.update(id, updateCampaignDto)).thenReturn(campaignDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(basePath + "/{campaignId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCampaignDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CampaignDto.Fields.title, Matchers.equalTo(campaignDto.title())))
                .andExpect(jsonPath(CampaignDto.Fields.status, Matchers.equalTo(campaignDto.status().toString())));
    }

    @Test
    void testGetByFilters() throws Exception {
        CampaignFilterDto campaignFilterDto
                = new CampaignFilterDto(LocalDateTime.now(), CampaignStatus.ACTIVE, 3L);

        CampaignDto campaignDtoOne = CampaignDto.builder()
                .createdBy(1L)
                .build();

        CampaignDto campaignDtoTwo = CampaignDto.builder()
                .createdBy(2L)
                .build();

        List<CampaignDto> expectedCampaignDtoList = new ArrayList<>(List.of(campaignDtoOne, campaignDtoTwo));

        when(campaignService.getByFilters(campaignFilterDto)).thenReturn(expectedCampaignDtoList);

        String response = mockMvc.perform(MockMvcRequestBuilders.get(basePath)
                        .param(CampaignFilterDto.Fields.createdAt, campaignFilterDto.createdAt().toString())
                        .param(CampaignFilterDto.Fields.status, String.valueOf(campaignFilterDto.status()))
                        .param(CampaignFilterDto.Fields.createdBy, campaignFilterDto.createdBy().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(expectedCampaignDtoList.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<CampaignDto> actualCampaigns = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CampaignDto.class));

        List<Long> expectedCreatedByIds = expectedCampaignDtoList.stream().map(CampaignDto::createdBy).toList();

        assertTrue(actualCampaigns.stream().map(CampaignDto::createdBy).toList().containsAll(expectedCreatedByIds));
    }

    @Test
    void testGetById() throws Exception {
        long id = 1L;

        CampaignDto campaignDto = CampaignDto.builder()
                .title("title new ")
                .build();

        when(campaignService.getCampaignById(id)).thenReturn(campaignDto);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/{campaignId}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CampaignDto.Fields.title, Matchers.equalTo(campaignDto.title())));
    }

    @Test
    void testSoftDelete() throws Exception {
        long campaignId = 43L;

        doNothing().when(campaignService).softDelete(campaignId);

        mockMvc.perform(MockMvcRequestBuilders.put(basePath + "/{campaignId}/softDelete", campaignId))
                .andExpect(status().isOk());

        verify(campaignService).softDelete(campaignId);
    }
}