package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService service;

    public ProjectDto createSubProject(ProjectDto projectDto) {
        validateProject(projectDto);
        return service.createSubProject(projectDto);
    }

    public ProjectDto updateSubProject(ProjectDto projectDto, UpdateSubProjectDto updateSubProjectDto) {
        validateProject(projectDto);
        return service.updateSubProject(projectDto, updateSubProjectDto);
    }

    public List<ProjectDto> getSubProject(ProjectDto projectDto, SubProjectFilterDto subProjectFilterDto) {
        validateProject(projectDto);
        return service.getSubProject(projectDto, subProjectFilterDto);
    }

    private void validateProject(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new IllegalArgumentException("Подпроект не может быть пустым");
        }
        if (projectDto.getId() == null) {
            throw new IllegalArgumentException("Подпроект не может иметь пустой ID");
        }
    }
}
