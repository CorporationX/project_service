package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.model.stage.StageRoles;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-08-02T16:34:15+0300",
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

    @Override
    public List<StageRoles> toListDto(List<StageRolesDto> stageRolesDtos) {
        if ( stageRolesDtos == null ) {
            return null;
        }

        List<StageRoles> list = new ArrayList<StageRoles>( stageRolesDtos.size() );
        for ( StageRolesDto stageRolesDto : stageRolesDtos ) {
            list.add( toEntity( stageRolesDto ) );
        }

        return list;
    }

    @Override
    public List<StageRolesDto> toListEntity(List<StageRoles> stageRoles) {
        if ( stageRoles == null ) {
            return null;
        }

        List<StageRolesDto> list = new ArrayList<StageRolesDto>( stageRoles.size() );
        for ( StageRoles stageRoles1 : stageRoles ) {
            list.add( toDto( stageRoles1 ) );
        }

        return list;
    }
}
