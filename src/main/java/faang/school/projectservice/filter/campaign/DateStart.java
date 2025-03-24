package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Campaign;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class DateStart implements Filter<Campaign> {

    private final LocalDateTime createdAt;

    @Override
    public boolean matches(Campaign campaign) {
        return createdAt == null || !createdAt.isAfter(campaign.getCreatedAt());
    }
}
