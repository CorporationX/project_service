package faang.school.projectservice.campaignFilterTest;

import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.filter.campaignFilter.CampaignStatusFilter;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CampaignStatusFilterTest {

    private CampaignStatusFilter campaignStatusFilter;

    @BeforeEach
    void setUp() {
        campaignStatusFilter = new CampaignStatusFilter();
    }

    @Test
    @DisplayName("Test with non null status filter")
    void testWithNonNullCreatedById() {
        CampaignFiltersDto campaignFiltersDto = new CampaignFiltersDto();
        campaignFiltersDto.setStatus(CampaignStatus.ACTIVE);

        assertTrue(campaignStatusFilter.isApplicable(campaignFiltersDto));
    }

    @Test
    @DisplayName("Test with null status filter")
    void testWithNullCreatedById() {
        CampaignFiltersDto campaignFiltersDto = new CampaignFiltersDto();

        assertFalse(campaignStatusFilter.isApplicable(campaignFiltersDto));
    }

    @Test
    @DisplayName("Test apply with matching status")
    public void testApplyWithMatchingCreatedById() {
        CampaignFiltersDto campaignFiltersDto = mock(CampaignFiltersDto.class);
        when(campaignFiltersDto.getStatus()).thenReturn(CampaignStatus.ACTIVE);

        Campaign firstCampaign = mock(Campaign.class);
        when(firstCampaign.getStatus()).thenReturn(CampaignStatus.ACTIVE);

        Campaign secondCampaign = mock(Campaign.class);
        when(secondCampaign.getStatus()).thenReturn(CampaignStatus.CANCELED);

        List<Campaign> campaigns = new ArrayList<>(List.of(firstCampaign, secondCampaign));

        List<Campaign> filteredCampaigns = campaigns.stream()
                .filter(campaign -> campaign.getStatus().equals(campaignFiltersDto.getStatus()))
                .toList();

        assertEquals(1, filteredCampaigns.size());
        assertEquals(filteredCampaigns.get(0).getStatus(), campaignFiltersDto.getStatus());
        assertEquals(filteredCampaigns, campaignStatusFilter.apply(campaigns, campaignFiltersDto).toList());
    }

    @Test
    @DisplayName("Test apply with non matching status")
    public void testApplyWithNonMatchingCreatedById() {
        CampaignFiltersDto campaignFiltersDto = mock(CampaignFiltersDto.class);
        when(campaignFiltersDto.getStatus()).thenReturn(CampaignStatus.ACTIVE);

        Campaign firstCampaign = mock(Campaign.class);
        when(firstCampaign.getStatus()).thenReturn(CampaignStatus.DELETED);

        Campaign secondCampaign = mock(Campaign.class);
        when(secondCampaign.getStatus()).thenReturn(CampaignStatus.CANCELED);

        List<Campaign> campaigns = new ArrayList<>(List.of(firstCampaign, secondCampaign));

        List<Campaign> filteredCampaigns = campaigns.stream()
                .filter(campaign -> campaign.getStatus().equals(campaignFiltersDto.getStatus()))
                .toList();

        assertEquals(0, filteredCampaigns.size());
        assertEquals(filteredCampaigns, campaignStatusFilter.apply(campaigns, campaignFiltersDto).toList());
    }
}
