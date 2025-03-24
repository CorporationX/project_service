package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Status implements Filter<Campaign> {

    private final CampaignStatus campaignStatus;

    @Override
    public boolean matches(Campaign campaign) {
        return campaignStatus == null || campaignStatus == campaign.getStatus();
    }
}
