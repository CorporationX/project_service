package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageViewDto;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

/**
 * StageMapper — маппер для стадий {@link faang.school.projectservice.model.stage.Stage}.
 * <p>
 *
 * </p>
 *
 * @author bozya
 * @since 31.07.2025
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {
    @Mapping(target = "stageId", ignore = true)
    Stage toEntity(StageCreateDto dto);

    @Mapping(target = "projectId", source = "project.id")
    StageViewDto toViewDto(Stage entity);
}