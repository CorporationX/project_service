package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "mapProjectsToIds")
    MomentDto toDto(Moment moment);
    List<MomentDto> toDto(List<Moment> momentList);

//    @Mapping(source = "projectIds", target = "projects")
    Moment toEntity(MomentDto momentDto);
    List<Moment> toEntity(List<MomentDto> momentDtoList);

    @Named("mapProjectsToIds")
    default List<Long> mapProjectsToIds(List<Project> projects){
        if (projects == null){
            return null;
        }
        return projects.stream()
                .map(Project::getId)
                .toList();
    }
//    @Named("mapIdsToProjects")
//    default List<Project> mapIdsToProjects(List<Long> projectsIds){
//        return projectsIds.stream()
//                .map((id)->{return
//                })
//                .toList();
//    }
}
