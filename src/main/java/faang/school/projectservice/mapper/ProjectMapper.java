package faang.school.projectservice.mapper;

<<<<<<< HEAD
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
=======
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
>>>>>>> titan-master-bc5
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    ProjectDto toDto(Project project);

    Project toEntity(CreateSubProjectDto createSubProjectDto);

    List<ProjectDto> toDtoList(List<Project> projectList);
}
