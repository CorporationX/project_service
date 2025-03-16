package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "teams", source = "teams", expression = "java(listIdTeams(project))")
    ProjectDto toDto(Project project);

    Project toEntity (ProjectDto projectDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProjectFromDto(ProjectDto dto, @MappingTarget Project entity);


    default List<Long> listIdTeams (Project project){
        return project.getTeams().stream().map(Team::getId).toList();
    }
}
