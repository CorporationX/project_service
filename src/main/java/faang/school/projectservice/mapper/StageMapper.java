package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stageRoles", target = "stageRolesDto", qualifiedByName = "toDtoStageRoles")
    StageResponse toResponse(Stage stage);

    List<StageResponse> toResponse(List<Stage> stages);

    @Mapping(source = "stageRolesDto", target = "stageRoles", qualifiedByName = "toEntityStageRolesDto")
    Stage toEntity(CreateStageRequest createStageRequest);

    default UpdateStageRequest validateUpdateStageRequest(UpdateStageRequest request) {
        if (request.requiredRoles() == null) {
            request = new UpdateStageRequest(
                    request.stageId(),
                    request.authorId(),
                    request.stageName(),
                    request.projectId(),
                    Collections.emptyList(),
                    request.executorsIds()
            );
        }
        if (request.executorsIds() == null) {
            request = new UpdateStageRequest(
                    request.stageId(),
                    request.authorId(),
                    request.stageName(),
                    request.projectId(),
                    request.requiredRoles(),
                    Collections.emptyList()
            );
        }
        return request;
    }

    void updateFromRequest(UpdateStageRequest updateStageRequest, @MappingTarget Stage stage);

    @Named("toDtoStageRoles")
    default StageRolesDto toDtoStageRoles(StageRoles stageRoles) {
        if (stageRoles == null) {
            return null;
        }
        return new StageRolesDto(
                stageRoles.getId(),
                stageRoles.getTeamRole(),
                stageRoles.getCount()
        );
    }

    @Named("toEntityStageRolesDto")
    default StageRoles toEntityStageRolesDto(StageRolesDto stageRolesDto) {
        if (stageRolesDto == null) {
            return null;
        }
        return StageRoles.builder()
                .id(stageRolesDto.id())
                .teamRole(stageRolesDto.teamRole())
                .count(stageRolesDto.count())
                .build();
    }
}
