package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignDtoMapper {
    CampaignDto toCampaignDto(Campaign campaign);

    Campaign toCampaign(CampaignDto campaignDto);

    List<CampaignDto> toListCampaignDto(List<Campaign> campaigns);

    List<Campaign> toListCampaign(List<CampaignDto> campaigns);
}
