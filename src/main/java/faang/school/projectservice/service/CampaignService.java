package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.CampaignDto;
import faang.school.projectservice.model.dto.CampaignFilterDto;

import java.util.List;

public interface CampaignService {
    CampaignDto create(CampaignDto campaignDto);

    CampaignDto update(long id, CampaignDto campaignDto);

    void delete(Long id);

    CampaignDto getById(Long id);

    List<CampaignDto> getByFilter(CampaignFilterDto filterDto);
}
