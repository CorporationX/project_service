package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(source = "projectIds", target = "projects", qualifiedByName = "getProjectFromId")
    Moment toEntity(MomentDto momentDto);

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "getIdFromProject")
    MomentDto toDto(Moment moment);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "sharingProjects", target = "projectIds", qualifiedByName = "getIdFromProject")
    @Mapping(source = "stages", target = "userIds", qualifiedByName = "getParticipantsIdsFromStages")
    @Mapping(source = "coverImageId", target = "imageId")
    MomentDto toDtoFromInitiative(Initiative initiative);

    @Named("getIdFromProject")
    default Long getIdFromProject(Project project) {
        return project.getId();
    }

    @Named("getProjectFromId")
    default Project getProjectFromId(Long id) {
        return Project.builder().id(id).build();
    }

    @Named("getParticipantsIdsFromStages")
    default List<Long> getParticipantsIdsFromStages(List<Stage> stages) {
        return stages.stream()
                .flatMap(stage -> stage.getExecutors().stream())
                .map(TeamMember::getUserId)
                .toList();
    }
}
