package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CampaignCreatedByFilterStrategyTest {
    private Campaign campaign = new Campaign();
    private CampaignFilterDto campaignFilterDto = new CampaignFilterDto();
    private CampaignCreatedByFilterStrategy campaignCreatedByFilterStrategy = new CampaignCreatedByFilterStrategy();

    @Test
    public void testFilter_when_Matches () {
        campaign.setCreatedBy(1L);
        campaignFilterDto.setCreatedBy(1L);
        Assertions.assertTrue(campaignCreatedByFilterStrategy.filter(campaign, campaignFilterDto));
    }

    @Test
    public void testFilter_when_NotMatches () {
        campaign.setCreatedBy(2L);
        campaignFilterDto.setCreatedBy(1L);
        Assertions.assertFalse(campaignCreatedByFilterStrategy.filter(campaign, campaignFilterDto));
    }

    @Test
    public void testIsApplicable_when_Null () {
        Assertions.assertFalse(campaignCreatedByFilterStrategy.isApplicable(campaignFilterDto));
    }

    @Test
    public void testIsApplicable_when_Present () {
        campaignFilterDto.setCreatedBy(1L);
        Assertions.assertTrue(campaignCreatedByFilterStrategy.isApplicable(campaignFilterDto));
    }
}
