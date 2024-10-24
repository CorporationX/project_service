package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.dto.CampaignUpdateDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {

    @Mapping(target = "createdBy", source = "creatorId")
    @Mapping(target = "project", source = "projectId")
    Campaign toEntity(CampaignDto campaignDto);

    @Mapping(target = "creatorId", source = "createdBy")
    @Mapping(target = "projectId", source = "project")
    CampaignDto toDto(Campaign campaign);

    void updateFromDto(CampaignUpdateDto dto, @MappingTarget Campaign campaign);

    default Project mapProjectId(Long projectId) {
        return projectId != null ?
                Project.builder().id(projectId).build() :
                null;
    }

    default Long mapProject(Project project) {
        return project != null ? project.getId() : null;
    }
}
