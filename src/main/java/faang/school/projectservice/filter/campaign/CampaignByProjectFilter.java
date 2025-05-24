package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignByProjectFilter implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignDto campaignDto) {
        return campaignDto.getProjectId() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaigns, CampaignDto campaignDto) {
        Long projectId = campaignDto.getProjectId();
        return campaigns
                .filter(campaign -> campaign.getProject().getId().equals(projectId));
    }
}
