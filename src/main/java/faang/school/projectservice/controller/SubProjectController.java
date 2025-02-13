package faang.school.projectservice.controller;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.SubProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class SubProjectController {
    private final SubProjectService subProjectService;

    public SubProjectDto createSubProject(CreateSubProjectDto createSubProjectDto) {
        log.info("Creating subproject {}", createSubProjectDto.toString());
        try {
            SubProjectDto createdSubProject = subProjectService.createSubProject(createSubProjectDto);
            log.info("Created subproject {}", createdSubProject.toString());
            return subProjectService.createSubProject(createSubProjectDto);
        } catch (Exception e) {
            log.error("Error creating subproject", e);
            throw new RuntimeException("Error creating subproject", e);
        }
    }

    public SubProjectDto updateSubProject(UpdateSubProjectDto updateSubProjectDto) {
        log.info("Updating subproject with params: {}", updateSubProjectDto.toString());
        try {
            SubProjectDto updatedSubProject = subProjectService.updateSubProject(updateSubProjectDto);
            return subProjectService.updateSubProject(updateSubProjectDto);
        } catch (Exception e) {
            log.error("Error updating subproject", e);
            throw new RuntimeException("Error updating subproject", e);
        }
    }

    public SubProjectDto getSubprojects(Project project, String filterName, ProjectStatus filterStatus) {
        log.info("Getting subprojects for project {}", project.getId());
        try {
            List<SubProjectDto> subProjectDtos = subProjectService.getSubprojects(project, filterName, filterStatus);
            if (subProjectDtos.isEmpty()) {
                log.warn("No subprojects found for project {}", project.getId());
                throw new RuntimeException("No subprojects found for project");
            }
            log.info("Found subprojects {}", subProjectDtos);
            return subProjectDtos.get(0);
        } catch (Exception e) {
            log.error("Error getting subprojects", e);
            throw new RuntimeException("Error getting subprojects", e);
        }
    }
}
