package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-02-04T21:12:41+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.9 (Amazon.com Inc.)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public ProjectDto toDto(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectDto.ProjectDtoBuilder projectDto = ProjectDto.builder();

        projectDto.id( project.getId() );
        projectDto.name( project.getName() );
        projectDto.description( project.getDescription() );
        projectDto.ownerId( project.getOwnerId() );
        projectDto.status( project.getStatus() );
        projectDto.visibility( project.getVisibility() );

        return projectDto.build();
    }

    @Override
    public Project toEntity(ProjectDto projectDto) {
        if ( projectDto == null ) {
            return null;
        }

        Project.ProjectBuilder project = Project.builder();

        project.id( projectDto.getId() );
        project.name( projectDto.getName() );
        project.description( projectDto.getDescription() );
        project.ownerId( projectDto.getOwnerId() );
        project.status( projectDto.getStatus() );
        project.visibility( projectDto.getVisibility() );

        return project.build();
    }
}
