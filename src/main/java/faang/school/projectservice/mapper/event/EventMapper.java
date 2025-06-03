package faang.school.projectservice.mapper.event;

import faang.school.projectservice.dto.event.EventOutputDto;
import faang.school.projectservice.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface EventMapper {
    @Mapping(target = "projectId", source = "project.id")
    EventOutputDto toEventOutputDto(Event event);
}