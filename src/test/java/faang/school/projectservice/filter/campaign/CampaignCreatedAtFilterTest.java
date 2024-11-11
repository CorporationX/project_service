package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CampaignCreatedAtFilterTest {

    @InjectMocks
    private CampaignCreatedAtFilter campaignCreatedAtFilter;
    private CampaignFilterDto campaignFilterDto;
    private Campaign campaign;
    private static final LocalDateTime TIME =
            LocalDateTime.of(2024, 10, 10, 10, 10);

    @Test
    @DisplayName("When filter createdAt field not null return true")
    public void whenCampaignFilterDtoCreatedAtIsNotNullThenReturnTrue() {
        campaignFilterDto = CampaignFilterDto.builder()
                .createdAt(TIME)
                .build();

        assertTrue(campaignCreatedAtFilter.isApplicable(campaignFilterDto));
    }

    @Test
    @DisplayName("When filter createdAt is null return false")
    public void whenCampaignFilterDtoCreatedAtIsNullThenReturnFalse() {
        campaignFilterDto = CampaignFilterDto.builder()
                .createdAt(null)
                .build();

        assertFalse(campaignCreatedAtFilter.isApplicable(campaignFilterDto));
    }

    @Test
    @DisplayName("When campaign createdAt field equals filter createdAt field return campaign")
    public void whenCampaignCreatedAtEqualsFilterCreatedAtFieldThenReturnCampaign() {
        campaignFilterDto = CampaignFilterDto.builder()
                .createdAt(TIME)
                .build();
        campaign = Campaign.builder()
                .createdAt(TIME)
                .build();

        Stream<Campaign> result = campaignCreatedAtFilter.applyFilter(Stream.of(campaign), campaignFilterDto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("When campaign createdAt is not equals filter createdAt field return empty stream")
    public void whenCampaignCreatedAtIsNotEqualsFilterCreatedAtFieldThenReturnEmptyStream() {
        campaignFilterDto = CampaignFilterDto.builder()
                .createdAt(TIME)
                .build();
        campaign = Campaign.builder()
                .createdAt(LocalDateTime.now())
                .build();

        Stream<Campaign> result = campaignCreatedAtFilter.applyFilter(Stream.of(campaign), campaignFilterDto);

        assertEquals(0, result.count());
    }
}