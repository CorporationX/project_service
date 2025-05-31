package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CampaignStatusFilterStrategyTest {
    private CampaignStatusFilterStrategy strategy = new CampaignStatusFilterStrategy();
    private Campaign campaign = new Campaign();
    private CampaignFilterDto campaignFilterDto =new CampaignFilterDto();

    @Test
    void testFilter_WhenMatches (){
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaignFilterDto.setStatus(CampaignStatus.ACTIVE);
        Assertions.assertTrue(strategy.filter(campaign, campaignFilterDto));
    }

    @Test
    void testFilter_WhenNotMatches (){
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaignFilterDto.setStatus(CampaignStatus.CANCELED);
        Assertions.assertFalse(strategy.filter(campaign, campaignFilterDto));
    }
}
