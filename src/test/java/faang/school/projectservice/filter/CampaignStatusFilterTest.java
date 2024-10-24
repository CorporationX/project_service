package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampaignStatusFilterTest {

    private final CampaignStatusFilter campaignStatusFilter = new CampaignStatusFilter();

    @Test
    void testIsAccepted_WithValidFilter() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setStatus(CampaignStatus.ACTIVE);

        assertTrue(campaignStatusFilter.isAccepted(filter));
    }

    @Test
    void testIsAccepted_WithNullStatus() {
        CampaignFilterDto filter = new CampaignFilterDto();

        assertFalse(campaignStatusFilter.isAccepted(filter));
    }

    @Test
    void testIsAccepted_WithNullFilter() {
        assertFalse(campaignStatusFilter.isAccepted(null));
    }

    @Test
    void testApply_WithMatchingStatus() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setStatus(CampaignStatus.ACTIVE);
        Stream<Campaign> campaignStream = getCampaignStream();

        Stream<Campaign> resultStream = campaignStatusFilter.apply(campaignStream, filter);
        List<Campaign> result = resultStream.toList();

        assertEquals(1, result.size());
        assertEquals(CampaignStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void testApply_WithNoMatchingStatus() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setStatus(CampaignStatus.COMPLETED);
        Stream<Campaign> campaignStream = getCampaignStream();

        Stream<Campaign> resultStream = campaignStatusFilter.apply(campaignStream, filter);
        List<Campaign> result = resultStream.toList();

        assertTrue(result.isEmpty());
    }

    private Stream<Campaign> getCampaignStream() {
        Campaign campaign1 = new Campaign();
        campaign1.setStatus(CampaignStatus.ACTIVE);
        Campaign campaign2 = new Campaign();
        campaign2.setStatus(CampaignStatus.CANCELED);
        return Stream.of(campaign1, campaign2);
    }
}
