package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    public CampaignDto getCampaignById(long id) {
        return campaignMapper.toDto(
                campaignRepository.findById(id)
                        .orElseThrow(
                                () -> new DataValidationException(String.format("Campaign %s not found", id))
                        )
        );
    }
}
