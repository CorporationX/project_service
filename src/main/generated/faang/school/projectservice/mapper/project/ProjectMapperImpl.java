package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-22T20:01:27+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public Project toEntity(ProjectDto dto) {
        if ( dto == null ) {
            return null;
        }

        Project.ProjectBuilder project = Project.builder();

        project.id( dto.getId() );
        project.name( dto.getName() );
        project.createdAt( dto.getCreatedAt() );
        project.updatedAt( dto.getUpdatedAt() );
        project.status( dto.getStatus() );
        project.visibility( dto.getVisibility() );

        return project.build();
    }

    @Override
    public ProjectDto toDto(Project entity) {
        if ( entity == null ) {
            return null;
        }

        ProjectDto.ProjectDtoBuilder projectDto = ProjectDto.builder();

        projectDto.childrenIds( getChildrenIds( entity.getChildren() ) );
        projectDto.stagesIds( getStagesIds( entity.getStages() ) );
        projectDto.teamsIds( getTeamsIds( entity.getTeams() ) );
        projectDto.id( entity.getId() );
        projectDto.name( entity.getName() );
        projectDto.createdAt( entity.getCreatedAt() );
        projectDto.updatedAt( entity.getUpdatedAt() );
        projectDto.status( entity.getStatus() );
        projectDto.visibility( entity.getVisibility() );

        return projectDto.build();
    }
}
