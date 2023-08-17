package faang.school.projectservice.mapper.invitationMaper;

import faang.school.projectservice.dto.invitation.DtoStage;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    StageMapper INSTANCE = Mappers.getMapper(StageMapper.class);

    Stage toStage(DtoStage dtoStage);

    DtoStage toDtoStage(Stage stage);
}
