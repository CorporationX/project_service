package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-19T09:17:06+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.5 (Oracle Corporation)"
)
@Component
public class StageRolesMapperImpl implements StageRolesMapper {

    @Override
    public StageRoles toEntity(StageRolesDto stageRolesDto) {
        if ( stageRolesDto == null ) {
            return null;
        }

        StageRoles.StageRolesBuilder stageRoles = StageRoles.builder();

        stageRoles.id( stageRolesDto.getId() );
        stageRoles.teamRole( stageRolesDto.getTeamRole() );
        stageRoles.count( stageRolesDto.getCount() );

        return stageRoles.build();
    }

    @Override
    public StageRolesDto toDto(StageRoles stageRoles) {
        if ( stageRoles == null ) {
            return null;
        }

        StageRolesDto.StageRolesDtoBuilder stageRolesDto = StageRolesDto.builder();

        stageRolesDto.id( stageRoles.getId() );
        stageRolesDto.teamRole( stageRoles.getTeamRole() );
        stageRolesDto.count( stageRoles.getCount() );

        return stageRolesDto.build();
    }
}
