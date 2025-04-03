package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
@ContextConfiguration(classes = {DonationController.class})
@ExtendWith(MockitoExtension.class)
class DonationControllerTest {

    private final List<DonationDto> donationDtoList = List.of(
            buildDonationDto(11L, BigDecimal.valueOf(1000L), Currency.USD),
            buildDonationDto(121L, BigDecimal.valueOf(2000L), Currency.EUR),
            buildDonationDto(12321L, BigDecimal.valueOf(3000L), Currency.EUR)
    );

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonationService donationService;


    @Test
    void testCreateDonation() throws Exception {

        DonationDto donationDto = donationDtoList.get(0);

        mockMvc.perform(post("/donation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donationDto))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void testGetDonation() throws Exception {

        DonationDto donationDto = donationDtoList.get(1);

        when(donationService.getDonationByIdAndUserId(donationDto.id())).thenReturn(donationDto);

        mockMvc.perform(
                get("/donation/getById/{id}",donationDto.id()))
                .andExpect(status().isOk());
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().json(objectMapper.writeValueAsString(donationDto)));
    }

    @Test
    void testGetAllDonations() throws Exception {

        DonationFilterDto donationFilterDto = new DonationFilterDto(
                null,
                null,
                null,
                null
        );
        when(donationService.getAllDonationsByUserId(donationFilterDto))
                .thenReturn(donationDtoList);

        mockMvc.perform(get("/donation/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donationFilterDto)))
                .andExpect(status().isOk());
//                .andExpect(content().json(objectMapper.writeValueAsString(donationDtoList)));
    }

    DonationDto buildDonationDto(Long donationId, BigDecimal amount, Currency currency) {

        return new DonationDto(
                donationId,
                null,
                amount,
                LocalDateTime.now(),
                2L,
                currency
        );
    }
}