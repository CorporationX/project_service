package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.donation.DonationViewDto;
import faang.school.projectservice.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.JpaMetamodelMappingContextFactoryBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DonationController.class)
class DonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JpaMetamodelMappingContextFactoryBean jpaMetamodelMappingContextFactoryBean;

    @MockBean
    private UserContext userContext;

    @MockBean
    private DonationService donationService;

    private final long userId = 1L;
    private final long donationId = 1L;
    private final DonationViewDto donationViewDto = new DonationViewDto();
    private final DonationCreateDto donationCreateDto = new DonationCreateDto();
    private final DonationFilterDto donationFilterDto = new DonationFilterDto();

    @BeforeEach
    void setUp() {
        donationViewDto.setId(donationId);
        donationViewDto.setUserId(userId);
    }

    @Test
    @DisplayName("sendDonation: валидный запрос - возвращает созданный донат")
    void testSendDonationValidRequest() throws Exception {
        Mockito.when(userContext.getUserId()).thenReturn(userId);
        Mockito.when(donationService.sendDonation(donationCreateDto, userId))
                .thenReturn(donationViewDto);

        donationCreateDto.setPaymentNumber(1L);
        donationCreateDto.setAmount(BigDecimal.valueOf(123.45));
        donationCreateDto.setCampaignId(1L);
        donationCreateDto.setCurrency(Currency.USD);

        mockMvc.perform(post("/donations/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donationCreateDto))
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(donationId))
                .andExpect(jsonPath("$.userId").value(userId));

        Mockito.verify(donationService, Mockito.times(1))
                .sendDonation(donationCreateDto, userId);
        }

        @Test
        @DisplayName("sendDonation: невалидный запрос - возвращает ошибку")
        void testSendDonationInvalidRequest() throws Exception {
            Mockito.when(userContext.getUserId()).thenReturn(userId);

            mockMvc.perform(post("/donations/send")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(donationCreateDto))
                            .header("x-user-id", userId))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("getDonationByIdForUser: валидный запрос - возвращает донат")
        void testGetDonationByIdForUserValidRequest() throws Exception {
            Mockito.when(userContext.getUserId()).thenReturn(userId);
            Mockito.when(donationService.getDonationByIdForUser(donationId, userId))
                    .thenReturn(donationViewDto);

            mockMvc.perform(get("/donations/{donationId}", (int) donationId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("x-user-id", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(donationId))
                    .andExpect(jsonPath("$.userId").value(userId));

            Mockito.verify(donationService, Mockito.times(1))
                    .getDonationByIdForUser(donationId, userId);
        }

        @Test
        @DisplayName("getUserDonations: валидный запрос - возвращает список донатов пользователя")
        void testGetUserDonationsValidRequest() throws Exception {
            Mockito.when(userContext.getUserId()).thenReturn(userId);
            Mockito.when(donationService.getUserDonations(userId, donationFilterDto))
                    .thenReturn(List.of(donationViewDto));

            mockMvc.perform(get("/donations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("x-user-id", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("[0].id").value(donationId))
                    .andExpect(jsonPath("[0].userId").value(userId));

            Mockito.verify(donationService, Mockito.times(1))
                    .getUserDonations(userId, donationFilterDto);
        }
}