package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class CampaignByCreateDateFilter implements CampaignFilter {
    @Override
    public boolean isApplicable(CampaignDto campaignDto) {
        return campaignDto.getCreatedFrom() != null || campaignDto.getCreatedTo() != null;
    }

    @Override
    public Stream<Campaign> apply(Stream<Campaign> campaigns, CampaignDto campaignDto) {
        LocalDateTime from = campaignDto.getCreatedFrom();
        LocalDateTime to = campaignDto.getCreatedTo();

        return campaigns
                .filter(campaign -> {
                    LocalDateTime createdAt = campaign.getCreatedAt();
                    if (from != null && createdAt.isBefore(from)) {
                        return false;
                    }
                    if (to != null && createdAt.isAfter(to)) {
                        return false;
                    }
                    return true;
                });
    }
}
