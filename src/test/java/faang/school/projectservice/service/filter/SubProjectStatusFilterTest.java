package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.filter.sub_project.SubProjectStatusFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестирование функциональности фильтрации подпроектов по статусу.
 * <p>
 * Содержит тесты для проверки корректности работы {@link SubProjectStatusFilter}:
 * <ul>
 *     <li>фильтрация проектов по различным статусам ({@link ProjectStatus})</li>
 *     <li>обработка граничных случаев (null-статус, пустая коллекция)</li>
 *     <li>проверка комбинации условий фильтрации</li>
 * </ul>
 * </p>
 *
 * @author Linempy
 * @since 24.07.2025
 */
@DisplayName("Тесты для фильтра по статусу подпроекта")
public class SubProjectStatusFilterTest {
    SubProjectStatusFilter filter = new SubProjectStatusFilter();


    @Test
    @DisplayName("issApplicable должен вернуть false, когда status null")
    public void shouldReturnFalse_WhenStatusIsNull() {
        SubProjectFilterDto dto = new SubProjectFilterDto(null, null);
        assertFalse(filter.isApplicable(dto));
    }

    @Test
    @DisplayName("issApplicable должен вернуть true, когда status не null")
    public void shouldReturnTrue_WhenStatusIsNotNull() {
        SubProjectFilterDto dto = new SubProjectFilterDto(null, ProjectStatus.IN_PROGRESS);
        assertTrue(filter.isApplicable(dto));
    }

    @Test
    @DisplayName("Граничный случай, когда передается пустой stream")
    public void shouldReturnEmptyStream_WhenInputStreamEmpty() {
        Stream<Project> projects = Stream.empty();
        SubProjectFilterDto filterDto = new SubProjectFilterDto("Name", ProjectStatus.CREATED);

        Stream<Project> result = filter.apply(projects, filterDto);

        assertEquals(0, result.count());
    }

    @Test
    @DisplayName("Должен вернуть подпроекты, которые имеют статус IN_PROGRESS")
    public void shouldReturnInProgressProjects_WhenFilterHaveStatusInProgress() {
        Stream<Project> projects = getProjects().stream();
        SubProjectFilterDto filterDto = new SubProjectFilterDto("Agu", ProjectStatus.IN_PROGRESS);

        List<Project> excepted = List.of(
                Project.builder().id(2L).status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().id(3L).status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().id(4L).status(ProjectStatus.IN_PROGRESS).build()
        );
        List<Project> result = filter.apply(projects, filterDto).toList();

        assertEquals(3, result.size());
        assertEquals(excepted, result);
    }

    @Test
    @DisplayName("Должен вернуть пустую коллекцию, когда в исходной нет проектов статуса CANCELLED")
    public void shouldReturnEmptyCollection_WhenFilterHaveStatusCancelled() {
        Stream<Project> projects = getProjects().stream();
        SubProjectFilterDto filterDto = new SubProjectFilterDto("Agu", ProjectStatus.CANCELLED);

        Stream<Project> result = filter.apply(projects, filterDto);

        assertEquals(0, result.count());
    }

    private List<Project> getProjects() {
        return List.of(
                Project.builder().id(1L).status(ProjectStatus.ON_HOLD).build(),
                Project.builder().id(2L).status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().id(3L).status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().id(4L).status(ProjectStatus.IN_PROGRESS).build(),
                Project.builder().id(5L).status(ProjectStatus.COMPLETED).build(),
                Project.builder().id(6L).status(ProjectStatus.COMPLETED).build(),
                Project.builder().id(7L).status(ProjectStatus.CREATED).build()
        );
    }

}