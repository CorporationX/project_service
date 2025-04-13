package school.faang.project_service.service.filter;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.filter.campaign.CampaignProjectFilter;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampaignProjectFilterTest {
    private static final Long PROJECT_ID = 1L;
    CampaignProjectFilter campaignProjectFilter = new CampaignProjectFilter();

    @Test
    public void testIsApplicableTrue() {
        CampaignFilterDto campaignFilterDto = prepareCampaignFilterDto(PROJECT_ID);
        boolean result = campaignProjectFilter.isApplicable(campaignFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApply() {
        Stream<Campaign> campaigns = Stream.of(
                Campaign.builder().project(Project.builder().id(PROJECT_ID).build()).build(),
                Campaign.builder().project(Project.builder().id(2L).build()).build()
        );

        Stream<Campaign> campaign = campaignProjectFilter.apply(campaigns,
                new CampaignFilterDto(PROJECT_ID, null, null, null));
        List<Campaign> campaignList = campaign.toList();
        assertEquals(1, campaignList.size());
        assertEquals(PROJECT_ID, campaignList.get(0).getProject().getId());
    }

    @Test
    public void testApplyWithNoSuitableCampaigns() {
        Stream<Campaign> campaigns = Stream.of(
                Campaign.builder().project(Project.builder().id(PROJECT_ID).build()).build(),
                Campaign.builder().project(Project.builder().id(2L).build()).build()
        );

        Stream<Campaign> campaign = campaignProjectFilter.apply(campaigns,
                new CampaignFilterDto(3L, null, null, null));
        List<Campaign> campaignList = campaign.toList();
        assertEquals(0, campaignList.size());
    }

    private CampaignFilterDto prepareCampaignFilterDto(Long projectID) {
        return CampaignFilterDto.builder()
                .status(null)
                .createdBy(null)
                .projectId(projectID)
                .createdAfter(null)
                .build();
    }
}
