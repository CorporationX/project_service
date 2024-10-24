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

class CreationDateToFilterTest {

    private final CreationDateToFilter creationDateToFilter = new CreationDateToFilter();

    @Test
    void testIsAccepted_WithValidFilter() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setCreatedTo(LocalDateTime.now().plusDays(1));

        assertTrue(creationDateToFilter.isAccepted(filter));
    }

    @Test
    void testIsAccepted_WithNullCreatedTo() {
        CampaignFilterDto filter = new CampaignFilterDto();

        assertFalse(creationDateToFilter.isAccepted(filter));
    }

    @Test
    void testIsAccepted_WithNullFilter() {
        assertFalse(creationDateToFilter.isAccepted(null));
    }

    @Test
    void testApply_WithMatchingDate() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setCreatedTo(LocalDateTime.now().plusDays(1));

        Stream<Campaign> campaignStream = getCampaignStream();

        Stream<Campaign> resultStream = creationDateToFilter.apply(campaignStream, filter);
        List<Campaign> result = resultStream.toList();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getCreatedAt().isBefore(filter.getCreatedTo()) ||
                result.get(0).getCreatedAt().isEqual(filter.getCreatedTo()));
    }

    @Test
    void testApply_WithNoMatchingDate() {
        CampaignFilterDto filter = new CampaignFilterDto();
        filter.setCreatedTo(LocalDateTime.now().minusDays(3));

        Stream<Campaign> campaignStream = getCampaignStream();

        Stream<Campaign> resultStream = creationDateToFilter.apply(campaignStream, filter);
        List<Campaign> result = resultStream.toList();

        assertTrue(result.isEmpty());
    }

    private Stream<Campaign> getCampaignStream() {
        Campaign campaign1 = new Campaign();
        campaign1.setCreatedAt(LocalDateTime.now().minusDays(2));

        Campaign campaign2 = new Campaign();
        campaign2.setCreatedAt(LocalDateTime.now().plusDays(1));

        return Stream.of(campaign1, campaign2);
    }
}
