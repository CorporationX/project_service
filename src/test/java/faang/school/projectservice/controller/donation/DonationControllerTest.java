package faang.school.projectservice.controller.donation;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.model.dto.client.Currency;
import faang.school.projectservice.model.dto.donation.DonationDto;
import faang.school.projectservice.service.DonationService;
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

import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
public class DonationControllerTest {

    @Mock
    private DonationService donationService;

    @InjectMocks
    private DonationController donationController;

    private MockMvc mockMvc;
    private DonationDto donationDto;
    private DonationDto badDonationDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(donationController).build();
        donationDto = DonationDto.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(300))
                .currency(Currency.USD)
                .campaignId(1L)
                .paymentNumber(10L)
                .build();

        badDonationDto = DonationDto.builder().build();
    }

    @Test
    void testSendDonationOk() throws Exception {
        when(donationService.sendDonation(any(DonationDto.class))).thenReturn(donationDto);

        String jsonDonation = objectMapper.writeValueAsString(donationDto);
        mockMvc.perform(post("/api/v1/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDonation))
                .andExpectAll(status().isCreated(),
                        jsonPath("$.campaignId").value(1L),
                        jsonPath("$.currency").value("USD"));
    }

    @Test
    void testSendDonationException() throws Exception {
        String jsonDonation = objectMapper.writeValueAsString(badDonationDto);
        mockMvc.perform(post("/api/v1/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDonation))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetDonationByIdOk() throws Exception {
        when(donationService.getDonationById(anyLong())).thenReturn(donationDto);

        mockMvc.perform(get("/api/v1/donations/{id}", 1))
                .andExpectAll(status().isOk(),
                        jsonPath("$.campaignId").value(1L),
                        jsonPath("$.currency").value("USD"));
    }

    @Test
    void testGetAllDonationsByUserId() throws Exception {
        List<DonationDto> donationDtos = List.of(donationDto);
        when(donationService.getAllDonationsByUserId(anyLong(), any())).thenReturn(donationDtos);

        mockMvc.perform(get("/api/v1/donations/users/{id}/all", 1))
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].campaignId").value(1L),
                        jsonPath("$[0].currency").value("USD"));
    }
}
