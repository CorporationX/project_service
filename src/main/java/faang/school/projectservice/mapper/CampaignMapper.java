package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    @Mapping(target = "project", ignore = true)
    Campaign toEntity(CampaignCreateDto dto);

    @Mapping(target = "project", ignore = true)
    Campaign toEntity(CampaignUpdateDto dto);

    @Mapping(target = "projectId", source = "project.id")
    CampaignUpdateDto toDto(Campaign entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Campaign entity, CampaignUpdateDto dto);
}
