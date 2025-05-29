package faang.school.projectservice.mapper.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    @Mapping(target = "project.id", source = "projectId")
    Campaign toEntity(CampaignDto campaignDto);

    @Mapping(target = "projectId", source = "project.id")
    CampaignDto toDto(Campaign campaign);
}
