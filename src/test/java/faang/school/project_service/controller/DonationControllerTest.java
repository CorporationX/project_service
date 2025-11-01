package faang.school.project_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.controller.donation.DonationController;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.CreateDonationDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.donation.DonationServiceImpl;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DonationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String basePath = "/donations";
    private MockMvc mockMvc;

    @Mock
    private DonationServiceImpl donationService;
    @InjectMocks
    private DonationController donationController;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(donationController).build();
    }

    @Test
    void testSend() throws Exception {
        CreateDonationDto createDonationDto = CreateDonationDto.builder()
                .amount(new BigDecimal("20.00"))
                .campaignId(2L)
                .paymentCurrency(Currency.EUR)
                .build();

        DonationDto donationDto = DonationDto.builder()
                .id(1L)
                .build();

        when(donationService.sendDonation(createDonationDto)).thenReturn(donationDto);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(DonationDto.Fields.id, Matchers.equalTo(donationDto.id().intValue())));
    }

    @Test
    void testGetDonationsByUserId() throws Exception {
        long userId = 1;
        DonationDto donationOne = DonationDto.builder()
                .id(1L)
                .build();

        DonationDto donationTwo = DonationDto.builder()
                .id(2L)
                .build();

        List<DonationDto> donationDtoList = List.of(donationOne, donationTwo);

        DonationFilterDto donationFilterDto = DonationFilterDto.builder()
                .currency(Currency.EUR)
                .build();

        when(donationService.getDonationsByUserId(userId, donationFilterDto)).thenReturn(donationDtoList);

        String response = mockMvc.perform(MockMvcRequestBuilders.get(basePath)
                        .param(DonationFilterDto.Fields.currency, String.valueOf(donationFilterDto.currency()))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(donationDtoList.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<DonationDto> actualDonations = objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructCollectionType(List.class, DonationDto.class));

        List<Long> expectedDonationsIds = donationDtoList.stream()
                .map(DonationDto::id)
                .sorted()
                .toList();
        List<Long> actualDonationsIds = actualDonations.stream()
                .map(DonationDto::id)
                .sorted()
                .toList();

        assertEquals(expectedDonationsIds, actualDonationsIds);
    }

    @Test
    void testGetDonationByIdAndUserID() throws Exception {
        long userId = 1L;
        long donationId = 2L;
        DonationDto donationOne = DonationDto.builder()
                .id(23L)
                .build();

        when(donationService.getDonationByIdAndUserId(donationId, userId)).thenReturn(donationOne);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/{id}", donationId)
                        .header("x-user-id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(DonationDto.Fields.id, Matchers.equalTo(donationOne.id().intValue())));
    }
}