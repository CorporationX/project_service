package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    @PutMapping("/cover/{projectId}")
    public ProjectDto addProjectCover(@PathVariable Long projectId, @RequestParam("file") MultipartFile file) {
        log.info("Запрос на добавление обложки для проекта с id={}", projectId);
        return projectService.addProjectCover(projectId, file);
    }

    @DeleteMapping("/cover/{projectId}")
    public ProjectDto deleteProjectCover(@PathVariable Long projectId) {
        log.info("Запрос на удаление обложки для проекта с id={}", projectId);
        return projectService.deleteProjectCover(projectId);
    }
}
