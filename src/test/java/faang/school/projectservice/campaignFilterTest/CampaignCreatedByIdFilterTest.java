package faang.school.projectservice.campaignFilterTest;

import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.filter.campaignFilter.CampaignCreatedByIdFilter;
import faang.school.projectservice.model.Campaign;
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

public class CampaignCreatedByIdFilterTest {

    private CampaignCreatedByIdFilter campaignCreatedByIdFilter;

    @BeforeEach
    void setUp() {
        campaignCreatedByIdFilter = new CampaignCreatedByIdFilter();
    }

    @Test
    @DisplayName("Test with non null createdById filter")
    void testWithNonNullCreatedById() {
        CampaignFiltersDto campaignFiltersDto = new CampaignFiltersDto();
        campaignFiltersDto.setCreatedById(1L);

        assertTrue(campaignCreatedByIdFilter.isApplicable(campaignFiltersDto));
    }

    @Test
    @DisplayName("Test with null createdById filter")
    void testWithNullCreatedById() {
        CampaignFiltersDto campaignFiltersDto = new CampaignFiltersDto();

        assertFalse(campaignCreatedByIdFilter.isApplicable(campaignFiltersDto));
    }

    @Test
    @DisplayName("Test apply with matching createdById")
    public void testApplyWithMatchingCreatedById() {
        CampaignFiltersDto campaignFiltersDto = mock(CampaignFiltersDto.class);
        when(campaignFiltersDto.getCreatedById()).thenReturn(1L);

        Campaign firstCampaign = mock(Campaign.class);
        when(firstCampaign.getCreatedBy()).thenReturn(1L);

        Campaign secondCampaign = mock(Campaign.class);
        when(secondCampaign.getCreatedBy()).thenReturn(2L);

        List<Campaign> campaigns = new ArrayList<>(List.of(firstCampaign, secondCampaign));

        List<Campaign> filteredCampaigns = campaigns.stream()
                .filter(campaign -> campaign.getCreatedBy().equals(campaignFiltersDto.getCreatedById()))
                .toList();

        assertEquals(1, filteredCampaigns.size());
        assertEquals(filteredCampaigns.get(0).getCreatedBy(), campaignFiltersDto.getCreatedById());
        assertEquals(filteredCampaigns, campaignCreatedByIdFilter.apply(campaigns, campaignFiltersDto).toList());
    }

    @Test
    @DisplayName("Test apply with non matching createdById")
    public void testApplyWithNonMatchingCreatedById() {
        CampaignFiltersDto campaignFiltersDto = mock(CampaignFiltersDto.class);
        when(campaignFiltersDto.getCreatedById()).thenReturn(1L);

        Campaign firstCampaign = mock(Campaign.class);
        when(firstCampaign.getCreatedBy()).thenReturn(3L);

        Campaign secondCampaign = mock(Campaign.class);
        when(secondCampaign.getCreatedBy()).thenReturn(2L);

        List<Campaign> campaigns = new ArrayList<>(List.of(firstCampaign, secondCampaign));

        List<Campaign> filteredCampaigns = campaigns.stream()
                .filter(campaign -> campaign.getCreatedBy().equals(campaignFiltersDto.getCreatedById()))
                .toList();

        assertEquals(0, filteredCampaigns.size());
        assertEquals(filteredCampaigns, campaignCreatedByIdFilter.apply(campaigns, campaignFiltersDto).toList());
    }
}
