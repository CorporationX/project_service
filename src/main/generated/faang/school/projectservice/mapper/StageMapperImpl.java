package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-28T15:36:36+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.5 (Oracle Corporation)"
)
@Component
public class StageMapperImpl implements StageMapper {

    @Autowired
    private StageRolesMapper stageRolesMapper;

    @Override
    public StageDto toDto(Stage stage) {
        if ( stage == null ) {
            return null;
        }

        StageDto.StageDtoBuilder stageDto = StageDto.builder();

        stageDto.stageId( stage.getStageId() );
        stageDto.stageName( stage.getStageName() );
        stageDto.project( projectToProjectDto( stage.getProject() ) );
        stageDto.stageRoles( stageRolesMapper.toListEntity( stage.getStageRoles() ) );

        return stageDto.build();
    }

    @Override
    public Stage toEntity(StageDto stageDto) {
        if ( stageDto == null ) {
            return null;
        }

        Stage.StageBuilder stage = Stage.builder();

        stage.stageId( stageDto.getStageId() );
        stage.stageName( stageDto.getStageName() );
        stage.project( projectDtoToProject( stageDto.getProject() ) );
        stage.stageRoles( stageRolesMapper.toListDto( stageDto.getStageRoles() ) );

        return stage.build();
    }

    protected ProjectDto projectToProjectDto(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectDto.ProjectDtoBuilder projectDto = ProjectDto.builder();

        projectDto.id( project.getId() );
        projectDto.name( project.getName() );
        projectDto.description( project.getDescription() );
        projectDto.createdAt( project.getCreatedAt() );
        projectDto.updatedAt( project.getUpdatedAt() );
        projectDto.status( project.getStatus() );

        return projectDto.build();
    }

    protected Project projectDtoToProject(ProjectDto projectDto) {
        if ( projectDto == null ) {
            return null;
        }

        Project.ProjectBuilder project = Project.builder();

        project.id( projectDto.getId() );
        project.name( projectDto.getName() );
        project.description( projectDto.getDescription() );
        project.createdAt( projectDto.getCreatedAt() );
        project.updatedAt( projectDto.getUpdatedAt() );
        project.status( projectDto.getStatus() );

        return project.build();
    }
}
