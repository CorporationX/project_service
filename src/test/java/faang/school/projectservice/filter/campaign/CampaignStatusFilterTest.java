package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CampaignStatusFilterTest {

    @InjectMocks
    private CampaignStatusFilter campaignStatusFilter;
    private CampaignFilterDto campaignFilterDto;
    private Campaign campaign;

    @Test
    @DisplayName("When filter status field not null return true")
    public void whenCampaignFilterDtoStatusIsNotNullThenReturnTrue() {
        campaignFilterDto = CampaignFilterDto.builder()
                .status(CampaignStatus.ACTIVE)
                .build();

        assertTrue(campaignStatusFilter.isApplicable(campaignFilterDto));
    }

    @Test
    @DisplayName("When filter status is null return false")
    public void whenCampaignFilterDtoStatusIsNullThenReturnFalse() {
        campaignFilterDto = CampaignFilterDto.builder()
                .status(null)
                .build();

        assertFalse(campaignStatusFilter.isApplicable(campaignFilterDto));
    }

    @Test
    @DisplayName("When campaign status equals filter status field return campaign")
    public void whenCampaignStatusEqualsFilterStatusFieldThenReturnCampaign() {
        campaignFilterDto = CampaignFilterDto.builder()
                .status(CampaignStatus.ACTIVE)
                .build();
        campaign = Campaign.builder()
                .status(CampaignStatus.ACTIVE)
                .build();

        Stream<Campaign> result = campaignStatusFilter.applyFilter(Stream.of(campaign), campaignFilterDto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("When campaign status is not equals filter status field return empty stream")
    public void whenCampaignStatusIsNotEqualsFilterStatusFieldThenReturnEmptyStream() {
        campaignFilterDto = CampaignFilterDto.builder()
                .status(CampaignStatus.ACTIVE)
                .build();
        campaign = Campaign.builder()
                .status(CampaignStatus.COMPLETED)
                .build();

        Stream<Campaign> result = campaignStatusFilter.applyFilter(Stream.of(campaign), campaignFilterDto);

        assertEquals(0, result.count());
    }
}