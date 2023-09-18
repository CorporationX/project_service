package faang.school.projectservice.filters.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampaignFilterByCreatedByTest {
    private CampaignFilterByCreatedBy campaignFilterByCreatedBy = new CampaignFilterByCreatedBy();
    private Long createdBy;
    private CampaignFilterDto campaignFilterDto;
    private CampaignFilterDto campaignFilterDtoWrong;

    private Campaign campaign;

    @BeforeEach
    void setUp() {
        createdBy = 1L;
        campaignFilterDto = CampaignFilterDto
                .builder()
                .createdBy(createdBy)
                .build();
        campaignFilterDtoWrong = CampaignFilterDto
                .builder()
                .build();
    }

    @Test
    public void isApplicable_Successful() {
        assertTrue(campaignFilterByCreatedBy.isApplicable(campaignFilterDto));
    }

    @Test
    public void isApplicable_NotSuccessful() {
        assertFalse(campaignFilterByCreatedBy.isApplicable(campaignFilterDtoWrong));
    }

    @Test
    public void apply_Successful() {
        Campaign campaign1 = Campaign
                .builder()
                .createdBy(1L)
                .build();
        Campaign campaign2 = Campaign
                .builder()
                .createdBy(2L)
                .build();
        Campaign campaign3 = Campaign
                .builder()
                .createdBy(3L)
                .build();

        List<Campaign> campaignList = List.of(campaign1, campaign2, campaign3);
        List<Campaign> list = campaignFilterByCreatedBy.apply(campaignList.stream(), campaignFilterDto).toList();

        assertEquals(1, list.size());
        assertEquals(createdBy, list.get(0).getCreatedBy());
    }
}
