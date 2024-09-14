package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MomentMapper {
    MomentDto toDto(Moment moment);
    Moment toEntity(MomentDto momentDto);


    default List<Long> map(Collection<Object> value) {
        return value.stream()
                .filter(Long.class::isInstance)
                .map(Long.class::cast)
                .collect(Collectors.toList());
    }
}