package faang.school.projectservice.model.mapper.campaign;

import faang.school.projectservice.model.dto.campaign.CampaignDto;
import faang.school.projectservice.model.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.entity.Campaign;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {

    @Mapping(source = "projectId", target = "project.id")
    Campaign toEntity(CampaignDto campaignDto);

    @Mapping(source = "project.id", target = "projectId")
    CampaignDto toDto(Campaign campaign);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Campaign campaign, CampaignUpdateDto updateDto);

    List<CampaignDto> toDtoList(List<Campaign> campaignList);
}
