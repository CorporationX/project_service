package faang.school.projectservice.repository.adapter.campaign;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CampaignRepoAdapter {
    private final CampaignRepository campaignRepository;

    public Campaign save(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public Campaign getCampaignById(Long campaignId) {
        Campaign existing = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found with Id " + campaignId));
        if (existing.getStatus() == CampaignStatus.CANCELED) {
            log.warn("Requesting for a Cancelled campaign with Id {}:", campaignId);
            throw new EntityNotFoundException("Campaign not found with Id " + campaignId);
        }
        return existing;
    }

    public List<Campaign> getAll() {
        return campaignRepository.findAll();
    }

    public Page<Campaign> getAllPages(Specification<Campaign> spec, Pageable pageable) {
        return campaignRepository.findAll(spec, pageable);
    }
}
