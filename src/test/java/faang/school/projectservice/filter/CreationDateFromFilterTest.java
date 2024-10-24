package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreationDateFromFilterTest {

    private final CreationDateFromFilter creationDateFromFilter = new CreationDateFromFilter();

    @Test
    void testIsAccepted_WithValidFilter() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setCreatedFrom(LocalDateTime.now().minusDays(1));

        assertTrue(creationDateFromFilter.isAccepted(filter));
    }

    @Test
    void testIsAccepted_WithNullCreatedFrom() {
        CampaignFilterDto filter = new CampaignFilterDto();

        assertFalse(creationDateFromFilter.isAccepted(filter));
    }

    @Test
    void testIsAccepted_WithNullFilter() {
        assertFalse(creationDateFromFilter.isAccepted(null));
    }

    @Test
    void testApply_WithMatchingDate() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setCreatedFrom(LocalDateTime.now().minusDays(1));
        Stream<Campaign> campaignStream = getCampaignStream();

        Stream<Campaign> resultStream = creationDateFromFilter.apply(campaignStream, filter);
        List<Campaign> result = resultStream.toList();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getCreatedAt().isAfter(filter.getCreatedFrom()) ||
                result.get(0).getCreatedAt().isEqual(filter.getCreatedFrom()));
    }

    @Test
    void testApply_WithNoMatchingDate() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setCreatedFrom(LocalDateTime.now().plusDays(1));

        Stream<Campaign> campaignStream = getCampaignStream();

        Stream<Campaign> resultStream = creationDateFromFilter.apply(campaignStream, filter);
        List<Campaign> result = resultStream.toList();

        assertTrue(result.isEmpty());
    }

    private Stream<Campaign> getCampaignStream() {
        Campaign campaign1 = new Campaign();
        campaign1.setCreatedAt(LocalDateTime.now().minusDays(2));

        Campaign campaign2 = new Campaign();
        campaign2.setCreatedAt(LocalDateTime.now());

        return Stream.of(campaign1, campaign2);
    }
}
