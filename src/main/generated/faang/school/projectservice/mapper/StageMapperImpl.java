package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-19T09:17:06+0300",
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
        stageDto.stageRoles( stageRolesListToStageRolesDtoList( stage.getStageRoles() ) );

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
        stage.stageRoles( stageRolesDtoListToStageRolesList( stageDto.getStageRoles() ) );

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

    protected List<StageRolesDto> stageRolesListToStageRolesDtoList(List<StageRoles> list) {
        if ( list == null ) {
            return null;
        }

        List<StageRolesDto> list1 = new ArrayList<StageRolesDto>( list.size() );
        for ( StageRoles stageRoles : list ) {
            list1.add( stageRolesMapper.toDto( stageRoles ) );
        }

        return list1;
    }

    protected Project projectDtoToProject(ProjectDto projectDto) {
        if ( projectDto == null ) {
            return null;
        }

        Project project = new Project();

        project.setId( projectDto.getId() );
        project.setName( projectDto.getName() );
        project.setDescription( projectDto.getDescription() );
        project.setCreatedAt( projectDto.getCreatedAt() );
        project.setUpdatedAt( projectDto.getUpdatedAt() );
        project.setStatus( projectDto.getStatus() );

        return project;
    }

    protected List<StageRoles> stageRolesDtoListToStageRolesList(List<StageRolesDto> list) {
        if ( list == null ) {
            return null;
        }

        List<StageRoles> list1 = new ArrayList<StageRoles>( list.size() );
        for ( StageRolesDto stageRolesDto : list ) {
            list1.add( stageRolesMapper.toEntity( stageRolesDto ) );
        }

        return list1;
    }
}
