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

public class CampaignFilterByStatusTest {
    private CampaignFilterByStatus campaignFilterByStatus = new CampaignFilterByStatus();
    private CampaignStatus campaignStatus;
    private CampaignFilterDto campaignFilterDto;
    private CampaignFilterDto campaignFilterDtoWrong;
    private Campaign campaign;

    @BeforeEach
    void setUp() {
        campaignStatus = CampaignStatus.ACTIVE;
        campaignFilterDto = CampaignFilterDto
                .builder()
                .campaignStatus(campaignStatus)
                .build();
        campaignFilterDtoWrong = CampaignFilterDto
                .builder()
                .build();
    }

    @Test
    public void isApplicable_Successful() {
        assertTrue(campaignFilterByStatus.isApplicable(campaignFilterDto));
    }

    @Test
    public void isApplicable_NotSuccessful() {
        assertFalse(campaignFilterByStatus.isApplicable(campaignFilterDtoWrong));
    }

    @Test
    public void apply_Successful() {
        Campaign campaign1 = Campaign
                .builder()
                .status(CampaignStatus.ACTIVE)
                .build();
        Campaign campaign2 = Campaign
                .builder()
                .status(CampaignStatus.CANCELED)
                .build();
        Campaign campaign3 = Campaign
                .builder()
                .status(CampaignStatus.COMPLETED)
                .build();

        List<Campaign> campaignList = List.of(campaign1, campaign2, campaign3);
        List<Campaign> list = campaignFilterByStatus.apply(campaignList.stream(), campaignFilterDto).toList();

        assertEquals(1, list.size());
        assertEquals(campaignStatus, list.get(0).getStatus());
    }
}
