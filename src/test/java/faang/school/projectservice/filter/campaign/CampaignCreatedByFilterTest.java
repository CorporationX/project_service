package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CampaignCreatedByFilterTest {

    @InjectMocks
    private CampaignCreatedByFilter campaignCreatedByFilter;
    private CampaignFilterDto campaignFilterDto;
    private Campaign campaign;
    private static final long ID = 1L;
    private static final long NEW_ID = 2L;

    @Test
    @DisplayName("When filter createdBy field not null return true")
    public void whenCampaignFilterDtoCreatedByIsNotNullThenReturnTrue() {
        campaignFilterDto = CampaignFilterDto.builder()
                .createdBy(ID)
                .build();

        assertTrue(campaignCreatedByFilter.isApplicable(campaignFilterDto));
    }

    @Test
    @DisplayName("When filter createdBy is null return false")
    public void whenCampaignFilterDtoCreatedByIsNullThenReturnFalse() {
        campaignFilterDto = CampaignFilterDto.builder()
                .createdBy(null)
                .build();

        assertFalse(campaignCreatedByFilter.isApplicable(campaignFilterDto));
    }

    @Test
    @DisplayName("When campaign createdBy field equals filter createdBy field return campaign")
    public void whenCampaignCreatedByEqualsFilterCreatedByFieldThenReturnCampaign() {
        campaignFilterDto = CampaignFilterDto.builder()
                .createdBy(ID)
                .build();
        campaign = Campaign.builder()
                .createdBy(ID)
                .build();

        Stream<Campaign> result = campaignCreatedByFilter.applyFilter(Stream.of(campaign), campaignFilterDto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("When campaign createdBy is not equals filter createdBy field return empty stream")
    public void whenCampaignCreatedByIsNotEqualsFilterCreatedByFieldThenReturnEmptyStream() {
        campaignFilterDto = CampaignFilterDto.builder()
                .createdBy(ID)
                .build();
        campaign = Campaign.builder()
                .createdBy(NEW_ID)
                .build();

        Stream<Campaign> result = campaignCreatedByFilter.applyFilter(Stream.of(campaign), campaignFilterDto);

        assertEquals(0, result.count());
    }
}