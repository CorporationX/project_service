package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import org.springframework.web.multipart.MultipartFile;

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
    ProjectViewDto createProject(ProjectCreateDto projectDto);

    /**
     * Обновление проекта
     *
     * @param id         айди обновляемого проекта
     * @param projectDto передаваемые данные, необходимые для обновления проекта
     * @return
     */
    ProjectViewDto updateProject(long id, ProjectUpdateDto projectDto);

    /**
     * Возвращает список целей, соответствующих указанным фильтрам.
     *
     * @param projectFilterDto параметры фильтрации
     * @return список проектов в виде List<{@link ProjectViewDto}>
     */
    List<ProjectViewDto> getByFilters(ProjectFilterDto projectFilterDto);

    /**
     * Получение проекта по его id
     *
     * @param id принимаемый id проекта
     * @return {@link ProjectViewDto} возвращаемый проект
     */
    ProjectViewDto getProjectById(long id);
}
