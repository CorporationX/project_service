package school.faang.project_service.service.filter;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.filter.campaign.CampaignDateFilter;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampaignDateFilterTest {
    private static final Long PROJECT_ID = 1L;
    CampaignDateFilter campaignDateFilter = new CampaignDateFilter();

    @Test
    public void testIsApplicableTrue() {
        CampaignFilterDto campaignFilterDto = prepareCampaignFilterDto(PROJECT_ID, LocalDateTime.now());
        boolean result = campaignDateFilter.isApplicable(campaignFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        CampaignFilterDto campaignFilterDto = prepareCampaignFilterDto(PROJECT_ID, null);
        boolean result = campaignDateFilter.isApplicable(campaignFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        LocalDateTime date1 = LocalDateTime.of(2025, 1, 1, 1, 1);
        LocalDateTime date2 = LocalDateTime.of(2025, 3, 10, 1, 1);
        LocalDateTime dateAfter = LocalDateTime.of(2025, 3, 1, 1, 1);
        Stream<Campaign> campaigns = Stream.of(
                Campaign.builder().createdAt(date1).build(),
                Campaign.builder().createdAt(date2).build()
        );

        Stream<Campaign> campaign = campaignDateFilter.apply(campaigns,
                new CampaignFilterDto(PROJECT_ID, null, null, dateAfter));
        List<Campaign> campaignList = campaign.toList();
        assertEquals(1, campaignList.size());
        assertEquals(date2, campaignList.get(0).getCreatedAt());
    }
    @Test
    public void testApplyWithNoSuitableCampaigns() {
        LocalDateTime date1 = LocalDateTime.of(2025, 1, 1, 1, 1);
        LocalDateTime date2 = LocalDateTime.of(2025, 3, 10, 1, 1);
        LocalDateTime dateAfter = LocalDateTime.of(2025, 3, 23, 1, 1);
        Stream<Campaign> campaigns = Stream.of(
                Campaign.builder().createdAt(date1).build(),
                Campaign.builder().createdAt(date2).build()
        );

        Stream<Campaign> campaign = campaignDateFilter.apply(campaigns,
                new CampaignFilterDto(PROJECT_ID, null, null, dateAfter));
        List<Campaign> campaignList = campaign.toList();
        assertEquals(0, campaignList.size());
    }

    private CampaignFilterDto prepareCampaignFilterDto(Long projectID, LocalDateTime date) {
        return CampaignFilterDto.builder()
                .status(null)
                .createdBy(null)
                .projectId(projectID)
                .createdAfter(date)
                .build();
    }
}
