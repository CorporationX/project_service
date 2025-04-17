package school.faang.project_service.service.filter;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.filter.campaign.CampaignOwnerFilter;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampaignOwnerFilterTest {
    private static final Long PROJECT_ID = 1L;
    CampaignOwnerFilter campaignOwnerFilter = new CampaignOwnerFilter();

    @Test
    public void testIsApplicableTrue() {
        CampaignFilterDto campaignFilterDto = prepareCampaignFilterDto(PROJECT_ID, 1L);
        boolean result = campaignOwnerFilter.isApplicable(campaignFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        CampaignFilterDto campaignFilterDto = prepareCampaignFilterDto(PROJECT_ID, null);
        boolean result = campaignOwnerFilter.isApplicable(campaignFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<Campaign> campaigns = Stream.of(
                Campaign.builder().createdBy(1L).build(),
                Campaign.builder().createdBy(2L).build()
        );

        Stream<Campaign> campaign = campaignOwnerFilter.apply(campaigns,
                new CampaignFilterDto(PROJECT_ID, 1L, null, null));
        List<Campaign> campaignList = campaign.toList();
        assertEquals(1, campaignList.size());
        assertEquals(1L, campaignList.get(0).getCreatedBy());
    }

    @Test
    public void testApplyWithNoSuitableCampaigns() {
        Stream<Campaign> campaigns = Stream.of(
                Campaign.builder().createdBy(1L).build(),
                Campaign.builder().createdBy(2L).build()
        );

        Stream<Campaign> campaign = campaignOwnerFilter.apply(campaigns,
                new CampaignFilterDto(PROJECT_ID, 3L, null, null));
        List<Campaign> campaignList = campaign.toList();
        assertEquals(0, campaignList.size());
    }

    private CampaignFilterDto prepareCampaignFilterDto(Long projectID, Long createdBy) {
        return CampaignFilterDto.builder()
                .status(null)
                .createdBy(createdBy)
                .projectId(projectID)
                .createdAfter(null)
                .build();
    }
}
