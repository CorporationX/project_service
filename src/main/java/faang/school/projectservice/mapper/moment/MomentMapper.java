package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.dto.moment.MomentDto;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MomentMapper {
    MomentDto toMomentDto(Moment moment);
    Moment toMoment(MomentDto momentDto);
    default List<Long> projectsToIdList(List<Project> projects) {
        return projects != null ? projects.stream()
                .map(Project::getId)
                .toList() : Collections.emptyList();
    }

    default List<Project> idsToProjectList(List<Long> projectIds) {
        return projectIds != null ? projectIds.stream()
                .map(projectId -> {
                    Project project = new Project();
                    project.setId(projectId);
                    return project;
                }).toList() : Collections.emptyList();
    }
    default List<Long> momentsToIdList(List<Moment> moments) {
        return moments != null ? moments.stream()
                .map(Moment::getId)
                .toList() : Collections.emptyList();
    }

    default List<Moment> idsToMomentList(List<Long> momentIds) {
        return momentIds != null ? momentIds.stream()
                .map(momentId -> {
                    Moment moment = new Moment();
                    moment.setId(momentId);
                    return moment;
                }).toList() : Collections.emptyList();
    }
}
