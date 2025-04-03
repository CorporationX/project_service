package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.mapper.campaign.CampaignMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @InjectMocks
    private CampaignService campaignService;

    @Mock
    private CampaignRepository campaignRepository;

    @Spy
    private CampaignMapperImpl campaignMapperImpl;

    Campaign campaign;

    CampaignDto expected;

    @BeforeEach
    void setUp() {
        Project project = new Project();
        project.setId(123L);
        campaign = new Campaign();
        campaign.setId(1L);                 // id
        campaign.setTitle("New Campaign");     // title
        campaign.setDescription("Description here"); // description
        campaign.setGoal(new BigDecimal("10000"));           // goal
        campaign.setAmountRaised(new BigDecimal("5000"));            // amountRaised
        campaign.setStatus(CampaignStatus.valueOf("ACTIVE"));          // status
        campaign.setProject(project);              // projectId
        campaign.setCurrency(Currency.valueOf("USD"));             // currency
        campaign.setCreatedAt(LocalDateTime.parse("2024-03-29T14:32:04"));      // createdAt
        campaign.setCreatedBy(456L);              // createdBy
        campaign.setUpdatedAt(LocalDateTime.parse("2024-03-30T14:32:04"));      // updatedAt
        campaign.setUpdatedBy(789L);              // updatedBy);
        expected = new CampaignDto(
                1L,                 // id
                "New Campaign",     // title
                "Description here", // description
                "10000",           // goal
                "5000",            // amountRaised
                "ACTIVE",          // status
                123L,              // projectId
                "USD",             // currency
                "2024-03-29T14:32:04",      // createdAt
                456L,              // createdBy
                "2024-03-30T14:32:04",      // updatedAt
                789L               // updatedBy
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetCampaignById() {
        when(campaignRepository.findById(campaign.getId())).thenReturn(Optional.of(campaign));

        CampaignDto actual = campaignService.getCampaignById(campaign.getId());

        assertEquals(expected, actual);

        verify(campaignMapperImpl, times(1)).toDto(campaign);
    }

    @Test
    void testGetCampaignByIdNotFound() {
        assertThrows(DataValidationException.class, () -> campaignService.getCampaignById(1));
    }
}