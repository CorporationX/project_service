package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.project.CreateProjectDto;
import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.dto.client.project.UpdateProjectDto;

import java.util.List;

/**
 * Интерфейс, предоставляющий операции по созданию, обновлению проекта и получению списка проектов
 * с фильтрами (по имени или статусу), без фильтров и проекта по айди.
 *
 * @author mrnght
 * @since 18.07.2025
 */
public interface ProjectService {

    /**
     * Создание проекта
     *
     * @param projectDto передаваемые данные, необходимые для создания проекта
     */
    void createProject(CreateProjectDto projectDto);

    /**
     * Обновление проекта
     *
     * @param id         айди обновляемого проекта
     * @param projectDto передаваемые данные, необходимые для обновления проекта
     */
    void updateProject(long id, UpdateProjectDto projectDto);

    /**
     * Получение списка проектов, отфильтрованных по статусу
     * {@link faang.school.projectservice.model.ProjectStatus}
     *
     * @param projectDto входные данные для выявления статуса проекта
     * @return возвращаемый лист, параметризованный {@link ProjectDto}
     */
    List<ProjectDto> getProjectsFilteredByStatus(ProjectDto projectDto);

    /**
     * Получение списка проектов, отфильтрованных по имени в алфавитном порядке
     *
     * @return возвращаемый лист, параметризованный {@link ProjectDto}
     */
    List<ProjectDto> getProjectsFilteredByName();

    /**
     * Получение списка всех проектов
     *
     * @return возвращаемый лист, параметризованный {@link ProjectDto}
     */
    List<ProjectDto> getAllProjects();

    /**
     * Получение проекта по его id
     *
     * @param id принимаемый id проекта
     * @return {@link ProjectDto} возвращаемый проект
     */
    ProjectDto getProjectById(long id);
}
