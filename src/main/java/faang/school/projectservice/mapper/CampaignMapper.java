package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "createdBy", target = "createdId")
    @Mapping(source = "updatedBy", target = "updatedId")
    CampaignDto toDto(Campaign entity);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "createdId", target = "createdBy")
    @Mapping(source = "updatedId", target = "updatedBy")
    @Mapping(target = "deleted", constant = "false")
    Campaign toEntity(CampaignDto dto);

    List<CampaignDto> toDtoList(List<Campaign> entities);
}
