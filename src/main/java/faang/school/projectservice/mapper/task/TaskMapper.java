package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.CreateTaskDto;
import faang.school.projectservice.dto.task.ResponseTaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "project.id", source = "projectId")
    Task createDtoToEntity(CreateTaskDto dto);

    @Mapping(target = "projectId", source = "project.id")
    ResponseTaskDto entityToResponseDto(Task task);

    List<ResponseTaskDto> entityListToDtoList(List<Task> tasks);
}