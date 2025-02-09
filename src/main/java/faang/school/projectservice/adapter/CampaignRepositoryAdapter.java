package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignRepositoryAdapter {
    private final CampaignRepository repository;

    public Campaign save(Campaign campaign) {
        return repository.save(campaign);
    }
}
