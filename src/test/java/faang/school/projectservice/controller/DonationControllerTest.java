package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {DonationController.class})
public class DonationControllerTest {

    private final Long firstId = 1L;
    private final BigDecimal firstAmount = new BigDecimal("10000.00");
    private final BigDecimal secondAmount = new BigDecimal("20000.00");
    private final BigDecimal thirdAmount = new BigDecimal("30000.00");
    private final Currency firstCurrency = Currency.USD;
    private final List<DonationDto> listDonations = List.of(
            createDonationDto(firstAmount, firstCurrency, firstId),
            createDonationDto(thirdAmount, firstCurrency, firstId)
    );
    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private DonationService donationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPositiveSendDonation() throws Exception {
        PaymentResponse paymentResponse = createDonationResponse(firstId, firstAmount, firstCurrency);
        String jsonBody = mapper.writeValueAsString(listDonations.get(0));
        when(donationService.sendDonation(listDonations.get(0))).thenReturn(paymentResponse);

        mockMvc.perform(post("/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(paymentResponse)));
    }

    @Test
    public void testPositiveFindDonationById() throws Exception {
        when(donationService.findDonationById(firstId)).thenReturn(listDonations.get(0));

        mockMvc.perform(get("/donations/{donationId}", firstId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(listDonations.get(0))));
    }

    @Test
    public void testPositiveFindDonationsByFilters() throws Exception {
        DonationFilterDto filter = createFilterDto(firstCurrency, firstAmount, secondAmount);
        String jsonBody = mapper.writeValueAsString(filter);
        List<DonationDto> returnedList = listDonations.stream()
                .filter(donation -> donation.currency().equals(filter.currency())
                        && donation.amount().compareTo(filter.minAmount()) >= 0
                        && donation.amount().compareTo(filter.maxAmount()) <= 0)
                .toList();
        when(donationService.findDonationsByFilters(filter)).thenReturn(returnedList);

        mockMvc.perform(post("/donations/all-filtered")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(returnedList)));
    }

    private DonationDto createDonationDto(BigDecimal amount, Currency currency, Long campaignId) {
        return DonationDto.builder()
                .amount(amount)
                .currency(currency)
                .campaignId(campaignId)
                .build();
    }

    private PaymentResponse createDonationResponse(Long number, BigDecimal amount, Currency currency) {
        return PaymentResponse.builder()
                .paymentNumber(number)
                .amount(amount)
                .paymentCurrency(currency)
                .build();
    }

    private DonationFilterDto createFilterDto(Currency currency, BigDecimal minAmount, BigDecimal maxAmount) {
        return DonationFilterDto.builder()
                .currency(currency)
                .maxAmount(maxAmount)
                .minAmount(minAmount)
                .build();
    }
}
