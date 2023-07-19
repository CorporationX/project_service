package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    MomentMapper INSTANCE = Mappers.getMapper(MomentMapper.class);

    @Mapping(source = "projects", target = "projects")
    MomentDto toDto(Moment moment);

    @Mapping(source = "projects", target = "projects")
    Moment toEntity(MomentDto momentDto);

    default Project projectFromProjectDto(ProjectDto projectDto) {
        if (projectDto == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectDto.getId());
        return project;
    }

    default List<ProjectDto> projectDtoFromProject(List<Project> projects) {
        return projects.stream()
                .map(project -> new ProjectDto(project.getId(), project.getStatus()))
                .toList();
    }
}
