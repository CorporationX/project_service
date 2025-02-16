package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {

    @Mapping(target = "projectId", source = "project.id")
    CampaignDto toDto(Campaign campaign);

    Campaign toEntity(CampaignDto dto);

    @Mapping(target = "amountRaised", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateCampaign(CampaignDto source, @MappingTarget Campaign target);
}
