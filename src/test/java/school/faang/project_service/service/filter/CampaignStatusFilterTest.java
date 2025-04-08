package school.faang.project_service.service.filter;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.filter.campaign.CampaignStatusFilter;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampaignStatusFilterTest {
    private static final Long PROJECT_ID = 1L;
    CampaignStatusFilter campaignStatusFilter = new CampaignStatusFilter();

    @Test
    public void testIsApplicableTrue() {
        CampaignFilterDto campaignFilterDto = prepareCampaignFilterDto(CampaignStatus.ACTIVE);
        boolean result = campaignStatusFilter.isApplicable(campaignFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        CampaignFilterDto campaignFilterDto = prepareCampaignFilterDto(null);
        boolean result = campaignStatusFilter.isApplicable(campaignFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<Campaign> campaigns = Stream.of(
                Campaign.builder().status(CampaignStatus.ACTIVE).build(),
                Campaign.builder().status(CampaignStatus.CANCELED).build()
        );

        Stream<Campaign> campaign = campaignStatusFilter.apply(campaigns,
                new CampaignFilterDto(PROJECT_ID, null, CampaignStatus.ACTIVE, null));
        List<Campaign> campaignList = campaign.toList();
        assertEquals(1, campaignList.size());
        assertEquals(CampaignStatus.ACTIVE, campaignList.get(0).getStatus());
    }

    @Test
    public void testApplyWithNoSuitableCampaigns() {
        Stream<Campaign> campaigns = Stream.of(
                Campaign.builder().status(CampaignStatus.ACTIVE).build(),
                Campaign.builder().status(CampaignStatus.CANCELED).build()
        );

        Stream<Campaign> campaign = campaignStatusFilter.apply(campaigns,
                new CampaignFilterDto(PROJECT_ID, null, CampaignStatus.COMPLETED, null));
        List<Campaign> campaignList = campaign.toList();
        assertEquals(0, campaignList.size());
    }

    private CampaignFilterDto prepareCampaignFilterDto(CampaignStatus status) {
        return CampaignFilterDto.builder()
                .status(status)
                .createdBy(null)
                .projectId(1L)
                .createdAfter(null)
                .build();
    }
}
