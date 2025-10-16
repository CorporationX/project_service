package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;

import java.util.List;

/**
 * ProjectService — описание класса.
 * <p>
 * * - Реализовать логику создания проекта с присвоением владельца, проверкой уникальности названия
 * * для пользователя и автоматическим статусом CREATED.
 * * - Реализовать обновление проекта: изменение описания, статуса, установка TIMESTAMP на каждое изменение.
 * * - Реализовать получение проектов с фильтрами по имени и статусу, с учетом приватности.
 * * - Реализовать получение всех проектов без фильтров.
 * * - Реализовать получение проекта по ID с проверкой приватности и прав пользователя.
 * * - Реализовать удаление проекта с проверкой прав.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 14.08.2025
 */
public interface ProjectService {
    ProjectDto createProject(ProjectDto projectDto);

    ProjectDto updateProject(Long projectId, ProjectDto projectDto);

    List<ProjectDto> getProjectsByFilter(ProjectDto projectDto);

    List<ProjectDto> getAllProjects();

    ProjectDto getProjectById(Long projectId);

    void deleteProject(Long projectId);
}