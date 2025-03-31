package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public void delete(long id) {
        campaignRepository.deleteById(id);
    }

    public List<Campaign> findAll(Specification<Campaign> specification) {
        return campaignRepository.findAll(specification);
    }
}
