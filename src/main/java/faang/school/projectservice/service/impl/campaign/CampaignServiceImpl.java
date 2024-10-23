package faang.school.projectservice.service.impl.campaign;

import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.jira.CampaignService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;

    @Override
    @Transactional(readOnly = true)
    public Campaign findCampaignById(long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign with id %s was not found"
                        .formatted(id)));
    }
}
