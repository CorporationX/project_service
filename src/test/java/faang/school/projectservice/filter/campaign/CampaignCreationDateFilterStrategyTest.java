package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CampaignCreationDateFilterStrategyTest {
   private CampaignCreationDateFilterStrategy strategy = new CampaignCreationDateFilterStrategy();
   private Campaign campaign = new Campaign();
   private CampaignFilterDto campaignFilterDto = new CampaignFilterDto ();

   @Test
    void testFilter_whenMatches () {
       campaign.setCreatedAt(LocalDateTime.now());
       campaignFilterDto.setCreatedAt(LocalDate.now());
       Assertions.assertTrue(strategy.filter(campaign, campaignFilterDto));
   }

   @Test
   void testFilter_whenNotMatches () {
      campaign.setCreatedAt(LocalDateTime.now());
      campaignFilterDto.setCreatedAt(LocalDate.now().minusDays(1));
      Assertions.assertFalse(strategy.filter(campaign, campaignFilterDto));
   }

   @Test
   public void testIsApplicable_when_Null () {
      Assertions.assertFalse(strategy.isApplicable(campaignFilterDto));
   }

   @Test
   public void testIsApplicable_when_Present () {
      campaignFilterDto.setCreatedAt(LocalDate.now());
      Assertions.assertTrue(strategy.isApplicable(campaignFilterDto));
   }
}
