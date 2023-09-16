package faang.school.projectservice.filters.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampaignFilterByCreatedAtTest {
    private CampaignFilterByCreatedAt campaignFilterByCreatedAt = new CampaignFilterByCreatedAt();
    private LocalDateTime now;
    private CampaignFilterDto campaignFilterDto;
    private CampaignFilterDto campaignFilterDtoWrong;
    private Campaign campaign;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        campaignFilterDto = CampaignFilterDto
                .builder()
                .createdAt(now)
                .build();
        campaignFilterDtoWrong = CampaignFilterDto
                .builder()
                .build();
    }

    @Test
    public void isApplicable_Successful() {
        assertTrue(campaignFilterByCreatedAt.isApplicable(campaignFilterDto));
    }

    @Test
    public void isApplicable_NotSuccessful() {
        assertFalse(campaignFilterByCreatedAt.isApplicable(campaignFilterDtoWrong));
    }

    @Test
    public void apply_Successful() {
        Campaign campaign1 = Campaign
                .builder()
                .createdAt(LocalDateTime.now().minusMonths(5))
                .build();
        Campaign campaign2 = Campaign
                .builder()
                .createdAt(LocalDateTime.now().minusDays(6))
                .build();
        Campaign campaign3 = Campaign
                .builder()
                .createdAt(now)
                .build();

        List<Campaign> campaignList = List.of(campaign1, campaign2, campaign3);
        List<Campaign> list = campaignFilterByCreatedAt.apply(campaignList.stream(), campaignFilterDto).toList();

        assertEquals(1, list.size());
        assertEquals(now, list.get(0).getCreatedAt());
    }
}