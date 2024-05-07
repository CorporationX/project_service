package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.initiative.InitiativeDto;
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
public interface InitiativeMapper {

    @Mapping(source = "initiativeDto.id", target = "id")
    @Mapping(source = "initiativeDto.name", target = "name")
    @Mapping(source = "initiativeDto.description", target = "description")
    @Mapping(source = "curator", target = "curator")
    @Mapping(source = "project", target = "project")
    @Mapping(source = "stages", target = "stages")
    @Mapping(source = "initiativeDto.status", target = "status")
    @Mapping(target = "sharingProjects", ignore = true)
    @Mapping(target = "coverImageId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Initiative toEntity(InitiativeDto initiativeDto, Project project, TeamMember curator, List<Stage> stages);

    @Mapping(source = "curator.userId", target = "curatorId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stages", target = "stageIds", qualifiedByName = "getIdFromStage")
    InitiativeDto toDto(Initiative initiative);

    @Named("getIdFromStage")
    default Long getIdFromStage(Stage stage) {
        return stage.getStageId();
    }
}
