package faang.school.projectservice.service;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;

    public Campaign findById(long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Компания c ID <%d> не найдена", id)));
    }

}
