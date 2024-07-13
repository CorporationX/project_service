package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.moment.MomentRequestDto;
import faang.school.projectservice.dto.client.moment.MomentResponseDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MomentMapper {

    @Mapping(source = "projects", target = "projects")
    Moment toEntity(MomentRequestDto momentRequestDto, List<Project> projects);


    @Mapping(source = "resource.id", target = "resourceIds")
    @Mapping(source = "projects.id", target = "projectsIds")
    MomentResponseDto toResponseDto(Moment moment);
}
