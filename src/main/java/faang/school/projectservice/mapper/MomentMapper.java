package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MomentMapper {

    @Mapping(source = "projects", target = "projects")
    Moment toEntity(MomentRequestDto momentRequestDto, List<Project> projects);


    //@Mapping(source = "resource", target = "resourceIds", qualifiedByName = "resourceToId")
    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "projectToId")
    MomentResponseDto toResponseDto(Moment moment);

    List<MomentResponseDto> toResponseDtoList(Collection<Moment> moments);

    @Named("resourceToId")
    default Long resourceToId(Resource resource) {
        return resource.getId();
    }

    @Named("projectToId")
    default Long projectToId(Project project) {
        return project.getId();
    }
}
