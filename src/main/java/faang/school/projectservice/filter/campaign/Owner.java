package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Campaign;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Owner implements Filter<Campaign> {

    private final Long  createdBy;

    @Override
    public boolean matches(Campaign campaign) {
        return createdBy == null || createdBy.equals(campaign.getCreatedBy());
    }
}
