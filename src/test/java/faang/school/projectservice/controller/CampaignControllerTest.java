package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.service.CampaignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {CampaignController.class})
public class CampaignControllerTest {

    private final String URL = "/api/v1/campaigns";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CampaignService service;
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    CampaignDto normalRequestDto = CampaignDto.builder()
            .title("title")
            .description("description")
            .goal(BigDecimal.valueOf(1))
            .status(CampaignStatus.ACTIVE)
            .projectId(1L)
            .currency(Currency.USD)
            .build();


    static Stream<Object[]> invalidRequestDtos() {
        return Stream.of(
                new Object[]{CampaignDto.builder()
                        .id(1L)
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title(null)
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title(" ")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description(null)
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description(" ")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(0))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(null)
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .amountRaised(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(null)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(0L)
                        .currency(Currency.USD)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(null)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .createdAt(LocalDateTime.now())
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .createdBy(1L)
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .updatedAt(LocalDateTime.now())
                        .build()},
                new Object[]{CampaignDto.builder()
                        .title("title")
                        .description("description")
                        .goal(BigDecimal.valueOf(1))
                        .status(CampaignStatus.ACTIVE)
                        .projectId(1L)
                        .currency(Currency.USD)
                        .updatedBy(1L)
                        .build()}
        );
    }

    @Test
    @DisplayName("Positive test for the normal CampaignDto. ")
    void createCampaignPositiveTest() throws Exception {

        String jsonRequestDto = OBJECT_MAPPER.writeValueAsString(normalRequestDto);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestDto))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource({"invalidRequestDtos"})
    @DisplayName("Test for all invalid requests")
    void negativeCreateCampaignTest(CampaignDto requestDto) throws Exception {
        String jsonRequestDto = OBJECT_MAPPER.writeValueAsString(requestDto);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestDto))
                .andExpect(status().isBadRequest());
    }
}
