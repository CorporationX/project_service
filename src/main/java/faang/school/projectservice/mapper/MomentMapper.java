package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MomentMapper {

    @Mapping(source = "projects", target = "projects")
    Moment toEntity(MomentRequestDto momentRequestDto, List<Project> projects);

    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "projectToId")
    MomentResponseDto toResponseDto(Moment moment);

    List<MomentResponseDto> toResponseDtoList(Collection<Moment> moments);

    @Named("projectToId")
    default Long projectToId(Project project) {
        return project.getId();
    }
}
