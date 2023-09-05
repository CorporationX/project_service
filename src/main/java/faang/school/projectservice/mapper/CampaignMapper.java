package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.company.CreateCampanyDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {
    @Mapping(target = "projectId", source = "project.id")
    CreateCampanyDto toDto(Campaign campaign);

    @Mapping(target = "project.id", source = "projectId")
    Campaign toEntity(CreateCampanyDto dto);
}
