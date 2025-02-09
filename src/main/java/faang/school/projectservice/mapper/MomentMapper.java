package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    MomentDto toDto(Moment moment);

    Moment toEntity(MomentDto momentDto);

    @AfterMapping
    default void setDefaultValues(@MappingTarget Moment moment) {
        if (moment.getCreatedAt() == null) {
            moment.setCreatedAt(LocalDateTime.now());
        }
        if (moment.getUpdatedAt() == null) {
            moment.setUpdatedAt(LocalDateTime.now());
        }
    }
}
