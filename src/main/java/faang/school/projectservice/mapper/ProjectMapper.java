package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "moments", target = "moments", qualifiedByName = "momentsToMomentsIds")
    ProjectDto toDto(Project project);

    @Mapping(source = "moments", target = "moments", qualifiedByName = "momentsIdsToMoments")
    Project toEntity(ProjectDto projectDto);

    @Named("momentsToMomentsIds")
    default List<Long> momentsToMomentsIds(List<Moment> moments) {
        if (moments == null) return null;
        return moments.stream()
                .map(Moment::getId)
                .toList();
    }

    @Named("momentsIdsToMoments")
    default List<Moment> momentsIdsToMoments(List<Long> momentsIds) {
        if (momentsIds == null) return null;
        return momentsIds.stream()
                .map(id -> {
                    Moment moment = new Moment();
                    moment.setId(id);
                    return moment;
                })
                .toList();
    }
}
