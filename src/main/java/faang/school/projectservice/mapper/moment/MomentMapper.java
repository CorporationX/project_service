package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(source = "resource", qualifiedByName = "toListRecourseId", target = "recourseId")
    @Mapping(source = "projects", qualifiedByName = "toListProjectId", target = "projectsId")
    MomentDto toDto(Moment moment);

    @Mapping(source = "recourseId", qualifiedByName = "toListRecourse", target = "resource")
    @Mapping(source = "projectsId", qualifiedByName = "toListProject", target = "projects")
    Moment toEntity(MomentDto momentDto);

    @Named("toListRecourseId")
    default List<Long> toListRecourseId(List<Resource> resources) {
        return resources.stream().map(Resource::getId).toList();
    }

    @Named("toListProjectId")
    default List<Long> toListProjectId(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }

    @Named("toListRecourse")
    default List<Resource> toListRecourse(List<Long> recourseId) {
        return recourseId.stream().map(id -> {
            var resource = new Resource();
            resource.setId(id);
            return resource;
        }).toList();
    }

    @Named("toListProject")
    default List<Project> toListProject(List<Long> projectsId) {
        return projectsId.stream().map(id -> {
            var project = new Project();
            project.setId(id);
            return project;
        }).toList();
    }
}
