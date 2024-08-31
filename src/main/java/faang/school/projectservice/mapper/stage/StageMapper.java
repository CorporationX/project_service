package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    StageDto toDto(Stage stage);

    @Mapping(source = "projectId", target = "project", qualifiedByName = "mapping id to project entity")
    Stage toEntity(StageDto stageDto, @Context ProjectRepository projectRepository);

    @Named("mapping id to project entity")
    default Project idToProject(Long id, @Context ProjectRepository projectRepository) {
        return projectRepository.getProjectById(id);
    }
}