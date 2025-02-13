package faang.school.projectservice.service.controller;

import faang.school.projectservice.controller.DonationController;
import faang.school.projectservice.dto.DonationDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.filter.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DonationControllerTest {

    @InjectMocks
    private DonationController donationController;

    @Mock
    private DonationService donationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(donationController).build();
    }

    @Test
    void sendDonation_shouldReturnCreatedDonation() throws Exception {
        DonationDto donationDto = DonationDto.builder()
                .id(1L)
                .userId(1L)
                .campaignId(1L)
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .paymentNumber(1L)
                .build();

        when(donationService.sendDonation(any(DonationDto.class))).thenReturn(donationDto);

        mockMvc.perform(post("/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"campaignId\":1,\"amount\":100.00,\"currency\":\"USD\",\"paymentNumber\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.campaignId").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.paymentNumber").value(1));
    }

    @Test
    void getDonation_shouldReturnDonation() throws Exception {
        DonationDto donationDto = DonationDto.builder()
                .id(1L)
                .userId(1L)
                .campaignId(1L)
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .paymentNumber(1L)
                .build();

        when(donationService.getDonation(anyLong(), anyLong())).thenReturn(donationDto);

        mockMvc.perform(get("/donations/donation/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.campaignId").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.paymentNumber").value(1));
    }

    @Test
    void getDonations_shouldReturnDonations() throws Exception {
        DonationDto donationDto = DonationDto.builder()
                .id(1L)
                .userId(1L)
                .campaignId(1L)
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .paymentNumber(1L)
                .build();

        when(donationService.getDonations(anyLong(), any(DonationFilterDto.class)))
                .thenReturn(Collections.singletonList(donationDto));

        mockMvc.perform(get("/donations/user/1")
                        .param("startDate", "2025-02-01T00:00:00")
                        .param("endDate", "2025-02-13T00:00:00")
                        .param("currency", "USD")
                        .param("minAmount", "50.00")
                        .param("maxAmount", "150.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].campaignId").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].currency").value("USD"))
                .andExpect(jsonPath("$[0].paymentNumber").value(1));
    }
}
