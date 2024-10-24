package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreatorIdFilterTest {

    private final CreatorIdFilter creatorIdFilter = new CreatorIdFilter();

    @Test
    void testIsAccepted_WithValidFilter() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setCreatorId(1L);

        assertTrue(creatorIdFilter.isAccepted(filter));
    }

    @Test
    void testIsAccepted_WithNullCreatorId() {
        CampaignFilterDto filter = new CampaignFilterDto();

        assertFalse(creatorIdFilter.isAccepted(filter));
    }

    @Test
    void testIsAccepted_WithNullFilter() {
        assertFalse(creatorIdFilter.isAccepted(null));
    }

    @Test
    void testApply_WithMatchingCreatorId() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setCreatorId(1L);

        Stream<Campaign> campaignStream = getCampaignStream();

        Stream<Campaign> resultStream = creatorIdFilter.apply(campaignStream, filter);
        List<Campaign> result = resultStream.toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCreatedBy());
    }

    @Test
    void testApply_WithNoMatchingCreatorId() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setCreatorId(2L);

        Stream<Campaign> campaignStream = getCampaignStream();

        Stream<Campaign> resultStream = creatorIdFilter.apply(campaignStream, filter);
        List<Campaign> result = resultStream.toList();

        assertTrue(result.isEmpty());
    }

    private Stream<Campaign> getCampaignStream() {
        Campaign campaign1 = new Campaign();
        campaign1.setCreatedBy(1L);

        Campaign campaign2 = new Campaign();
        campaign2.setCreatedBy(3L);

        return Stream.of(campaign1, campaign2);
    }
}
