package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ProjectService {

    ProjectDto create(ProjectDto projectDto);

    ProjectDto update(ProjectDto projectDto);

    List<ProjectDto> getAll();

    ProjectDto findById(@PathVariable Long id);

    List<ProjectDto> getAllByFilter(ProjectFilterDto filterDto);

}
