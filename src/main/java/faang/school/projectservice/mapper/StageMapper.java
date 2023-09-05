//package faang.school.projectservice.mapper;
//
//import faang.school.projectservice.dto.stage.StageDto;
//import faang.school.projectservice.model.stage.Stage;
//import org.mapstruct.Mapper;
//import org.mapstruct.ReportingPolicy;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
//public interface StageMapper {
//    StageMapper INSTANCE = Mappers.getMapper(StageMapper.class);
//    List<Stage> toStageList(List<StageDto> stageDto);
//    List<StageDto> toStageDtoList(List<Stage> stage);
//    Stage toStage(StageDto stageDto);
//    StageDto toStageDto(Stage stage);
//}