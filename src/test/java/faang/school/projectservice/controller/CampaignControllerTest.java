package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.service.CampaignService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserContext.class, CampaignController.class})
@WebMvcTest
public class CampaignControllerTest {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final static String BASE_URL = "/campaign/";
    private final static String CREATE_URL = BASE_URL + "create";
    private final static String UPDATE_URL = BASE_URL + "{id}";
    private final static String DELETE_URL = BASE_URL + "{id}";
    private final static String FIND_URL = BASE_URL + "{id}";
    private final static String FILTER_URL = BASE_URL + "filter";

    @MockBean
    private CampaignService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateBadRequest() throws Exception {
        mockMvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(getCampaignCreateDtoWithoutRequiredFields())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateSuccess() throws Exception {
        when(service.create(any(CampaignCreateDto.class))).thenReturn(getCampaignUpdateDto());

        mockMvc.perform(post(CREATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(getCampaignCreateDto())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(getCampaignUpdateDto())))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateBadRequest() throws Exception {
        mockMvc.perform(put(UPDATE_URL, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(getCampaignUpdateDtoWithoutId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateSuccess() throws Exception {
        when(service.update(any(Long.class), any(CampaignUpdateDto.class))).thenReturn(getCampaignUpdateDto());

        mockMvc.perform(put(UPDATE_URL, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(getCampaignUpdateDto())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(getCampaignUpdateDto())))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        when(service.delete(any(Long.class))).thenReturn(getCampaignUpdateDto());

        mockMvc.perform(delete(DELETE_URL, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(getCampaignUpdateDto())))
                .andExpect(status().isOk());
    }

    @Test
    void testFindById() throws Exception {
        when(service.findById(any(Long.class))).thenReturn(getCampaignUpdateDto());

        mockMvc.perform(get(FIND_URL, "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(getCampaignUpdateDto())))
                .andExpect(status().isOk());
    }

    @Test
    void testFilter() throws Exception {
        List<CampaignUpdateDto> campaignUpdateDtos = List.of(getCampaignUpdateDto(), getCampaignUpdateDto());
        when(service.getFilteredCampaigns(any(CampaignFilterDto.class))).thenReturn(campaignUpdateDtos);

        mockMvc.perform(post(FILTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(getCampaignFilterDto())))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(campaignUpdateDtos)))
                .andExpect(status().isOk());
    }

    private CampaignUpdateDto getCampaignUpdateDto() {
        return buildBaseCampaignUpdateDto(1L);
    }

    private CampaignUpdateDto getCampaignUpdateDtoWithoutId() {
        return buildBaseCampaignUpdateDto(null);
    }

    private CampaignUpdateDto buildBaseCampaignUpdateDto(Long id) {
        return CampaignUpdateDto.builder()
                .id(id)
                .title("test title")
                .description("test description")
                .projectId(5L)
                .updatedBy(1L)
                .createdBy(1L)
                .status(CampaignStatus.COMPLETED)
                .goal(new BigDecimal("1"))
                .currency(Currency.USD)
                .build();
    }

    private CampaignCreateDto getCampaignCreateDto() {
        return CampaignCreateDto.builder()
                .title("test title")
                .description("test description")
                .projectId(5L)
                .updatedBy(1L)
                .createdBy(1L)
                .status(CampaignStatus.COMPLETED)
                .goal(new BigDecimal("1"))
                .currency(Currency.USD)
                .build();
    }

    private CampaignCreateDto getCampaignCreateDtoWithoutRequiredFields() {
        return CampaignCreateDto.builder()
                .status(CampaignStatus.COMPLETED)
                .goal(new BigDecimal("1"))
                .currency(Currency.USD)
                .build();
    }

    private CampaignFilterDto getCampaignFilterDto() {
        return new CampaignFilterDto("2022-01-01", CampaignStatus.ACTIVE, 1L);
    }

}
