package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    StageDto toDto(Stage stage);

    Stage toEntity(StageDto stageDto);

    @Named("mapStringToRequestStatus")
    default StageStatus mapToRequestStatus(String status) {
        return StageStatus.valueOf(status.toUpperCase());
    }

    @Named("mapRequestStatusToString")
    default String mapFromRequestStatus(StageStatus status) {
        return status.toString().toUpperCase();
    }
}