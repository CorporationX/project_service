package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = StageRolesMapper.class)
public interface StageMapper {
    @Mapping(target = "projectId", source = "project.id")
//    @Mapping(target = "status", source = "status", qualifiedByName = "mapRequestStatusToString")
    StageDto toDto(Stage stage);

    @Mapping(target = "project.id", source = "projectId")
//    @Mapping(target = "status", source = "status", qualifiedByName = "mapStringToRequestStatus")
    Stage toEntity(StageDto stageDto);

//    @Named("mapStringToRequestStatus")
//    default StageStatus mapToRequestStatus(String status) {
//        return StageStatus.valueOf(status.toUpperCase());
//    }
//
//    @Named("mapRequestStatusToString")
//    default String mapFromRequestStatus(StageStatus status) {
//        return status.toString().toUpperCase();
//    }
}