package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-22T20:01:27+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class SubProjectMapperImpl implements SubProjectMapper {

    @Override
    public Project toEntity(CreateSubProjectDto subProjectDto) {
        if ( subProjectDto == null ) {
            return null;
        }

        Project.ProjectBuilder project = Project.builder();

        project.id( subProjectDto.getId() );
        project.name( subProjectDto.getName() );
        project.createdAt( subProjectDto.getCreatedAt() );
        project.updatedAt( subProjectDto.getUpdatedAt() );
        project.status( subProjectDto.getStatus() );
        project.visibility( subProjectDto.getVisibility() );

        return project.build();
    }

    @Override
    public CreateSubProjectDto toDto(Project subProject) {
        if ( subProject == null ) {
            return null;
        }

        CreateSubProjectDto.CreateSubProjectDtoBuilder createSubProjectDto = CreateSubProjectDto.builder();

        createSubProjectDto.childrenIds( getChildrenIds( subProject.getChildren() ) );
        createSubProjectDto.stagesIds( getStagesIds( subProject.getStages() ) );
        createSubProjectDto.teamsIds( getTeamIds( subProject.getTeams() ) );
        createSubProjectDto.momentsIds( getMomentsIds( subProject.getMoments() ) );
        createSubProjectDto.parentProjectId( subProjectParentProjectId( subProject ) );
        createSubProjectDto.id( subProject.getId() );
        createSubProjectDto.name( subProject.getName() );
        createSubProjectDto.createdAt( subProject.getCreatedAt() );
        createSubProjectDto.updatedAt( subProject.getUpdatedAt() );
        createSubProjectDto.status( subProject.getStatus() );
        createSubProjectDto.visibility( subProject.getVisibility() );

        return createSubProjectDto.build();
    }

    private Long subProjectParentProjectId(Project project) {
        if ( project == null ) {
            return null;
        }
        Project parentProject = project.getParentProject();
        if ( parentProject == null ) {
            return null;
        }
        Long id = parentProject.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
