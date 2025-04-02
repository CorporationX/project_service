package faang.school.projectservice.service.campaign;

import faang.school.projectservice.exception.campaign.CampaignExceptionMessage;
import faang.school.projectservice.exception.campaign.CampaignNotFoundException;
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
                .orElseThrow(() -> new CampaignNotFoundException(CampaignExceptionMessage.getNotFound(id)));
    }
}
