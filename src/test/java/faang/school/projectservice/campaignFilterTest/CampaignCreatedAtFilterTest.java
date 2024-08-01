package faang.school.projectservice.campaignFilterTest;

import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.filter.campaignFilter.CampaignCreatedAtFilter;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CampaignCreatedAtFilterTest {

    private CampaignCreatedAtFilter campaignCreatedAtFilter;

    @BeforeEach
    void setUp() {
        campaignCreatedAtFilter = new CampaignCreatedAtFilter();
    }

    @Test
    @DisplayName("Test with non null createdAt filter")
    void testWithNonNullCreatedAt() {
        CampaignFiltersDto campaignFiltersDto = new CampaignFiltersDto();
        campaignFiltersDto.setCreatedAt(LocalDateTime.now().minusDays(1));

        assertTrue(campaignCreatedAtFilter.isApplicable(campaignFiltersDto));
    }

    @Test
    @DisplayName("Test with null createdAt filter")
    void testWithNullCreatedAt() {
        CampaignFiltersDto campaignFiltersDto = new CampaignFiltersDto();

        assertFalse(campaignCreatedAtFilter.isApplicable(campaignFiltersDto));
    }

    @Test
    @DisplayName("Test apply with matching createdAt")
    public void testApplyWithMatchingCreatedAt() {
        CampaignFiltersDto campaignFiltersDto = mock(CampaignFiltersDto.class);
        when(campaignFiltersDto.getCreatedAt()).thenReturn(LocalDateTime.now());

        Campaign firstCampaign = mock(Campaign.class);
        when(firstCampaign.getCreatedAt()).thenReturn(LocalDateTime.now().plusDays(1));

        Campaign secondCampaign = mock(Campaign.class);
        when(secondCampaign.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(1));

        List<Campaign> campaigns = new ArrayList<>(List.of(firstCampaign, secondCampaign));

        List<Campaign> filteredCampaigns = campaigns.stream()
                .filter(campaign -> campaign.getCreatedAt().isAfter(campaignFiltersDto.getCreatedAt()))
                .toList();

        assertEquals(1, filteredCampaigns.size());
        assertTrue(filteredCampaigns.get(0).getCreatedAt().isAfter(campaignFiltersDto.getCreatedAt()));
        assertEquals(filteredCampaigns, campaignCreatedAtFilter.apply(campaigns, campaignFiltersDto).toList());
    }

    @Test
    @DisplayName("Test apply with non matching createdAt")
    public void testApplyWithNonMatchingCreatedAt() {
        CampaignFiltersDto campaignFiltersDto = mock(CampaignFiltersDto.class);
        when(campaignFiltersDto.getCreatedAt()).thenReturn(LocalDateTime.now());

        Campaign firstCampaign = mock(Campaign.class);
        when(firstCampaign.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(2));

        Campaign secondCampaign = mock(Campaign.class);
        when(secondCampaign.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(1));

        List<Campaign> campaigns = new ArrayList<>(List.of(firstCampaign, secondCampaign));

        List<Campaign> filteredCampaigns = campaigns.stream()
                .filter(campaign -> campaign.getCreatedAt().isAfter(campaignFiltersDto.getCreatedAt()))
                .toList();

        assertEquals(0, filteredCampaigns.size());
        assertEquals(filteredCampaigns, campaignCreatedAtFilter.apply(campaigns, campaignFiltersDto).toList());
    }

}
