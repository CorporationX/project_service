package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignRepositoryAdapter {
    private final CampaignRepository campaignRepository;

    public Campaign findById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found with id: " + id));
    }

    public Campaign save(Campaign campaign) {
        return campaignRepository.save(campaign);
    }
}
