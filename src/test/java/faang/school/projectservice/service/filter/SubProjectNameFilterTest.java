package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.filter.sub_project.SubProjectNameFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестирование функциональности фильтрации подпроектов по имени.
 * <p>
 * Проверяет корректность работы {@link SubProjectNameFilter} при различных условиях:
 * <ul>
 *     <li>валидные и невалидные имена для фильтрации</li>
 *     <li>обработка пустых коллекций</li>
 *     <li>регистронезависимый поиск</li>
 * </ul>
 * </p>
 *
 * @author Linempy
 * @since 24.07.2025
 */
@DisplayName("Проверка фильтра по имени подпроекта")
public class SubProjectNameFilterTest {

    SubProjectNameFilter filter = new SubProjectNameFilter();

    @Test
    @DisplayName("Фильтр не применяется, когда имя пустое")
    public void shouldReturnFalse_WhenNameIsEmpty() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto("", null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("Фильтр не применяется, когда имя состоит из пробелов")
    public void shouldReturnFalse_WhenNameIsSpaces() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto("   ", ProjectStatus.CANCELLED);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("Фильтр не применяется, когда имя null")
    public void shouldReturnFalse_WhenNameIsNull() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto(null, ProjectStatus.CANCELLED);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("Фильтр применяется для валидного имени")
    public void shouldReturnTrue_WhenNameIsValid() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto("Name", ProjectStatus.IN_PROGRESS);
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("Находит проекты, содержащие 'test' (регистронезависимо)")
    public void shouldReturnProjectsContainTest_WhenNameHaveDifferentRegistry() {
        Stream<Project> projects = getProjects().stream();
        SubProjectFilterDto filterDto = new SubProjectFilterDto("tEsT", null);
        List<Project> expected = List.of(
                Project.builder().id(5L).name("  Test  ").build(),
                Project.builder().id(6L).name(" some test ").build(),
                Project.builder().id(7L).name("too some test").build()
        );

        List<Project> result = filter.apply(projects, filterDto).toList();

        assertEquals(3, result.size());
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Находит проекты, содержащие 'test'")
    public void shouldReturnProjectsContainTest_WhenNameIsTest() {
        Stream<Project> projects = getProjects().stream();
        SubProjectFilterDto filterDto = new SubProjectFilterDto("test", null);
        List<Project> expected = List.of(
                Project.builder().id(5L).name("  Test  ").build(),
                Project.builder().id(6L).name(" some test ").build(),
                Project.builder().id(7L).name("too some test").build()
        );

        List<Project> result = filter.apply(projects, filterDto).toList();

        assertEquals(3, result.size());
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Находит проект, содержащий 'blank'")
    public void shouldReturnProjectContainsBlank_WhenNameEqualsBlank() {
        Stream<Project> projects = getProjects().stream();
        SubProjectFilterDto filterDto = new SubProjectFilterDto("BLANK", null);
        List<Project> expected = List.of(
                Project.builder().id(4L).name("Not blank").build()
        );

        List<Project> result = filter.apply(projects, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Возвращает пустой список, если имя не найдено")
    public void shouldReturnEmptyCollection_WhenNameNotContainsInProjectName() {
        Stream<Project> projects = getProjects().stream();
        SubProjectFilterDto filterDto = new SubProjectFilterDto("Unique", ProjectStatus.IN_PROGRESS);

        List<Project> result = filter.apply(projects, filterDto).toList();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Возвращает пустой список для пустой коллекции проектов")
    public void shouldReturnEmptyCollection_WhenInputCollectionIsEmpty() {
        Stream<Project> projects = Stream.empty();
        SubProjectFilterDto filterDto = new SubProjectFilterDto("Empty Collection", ProjectStatus.IN_PROGRESS);

        List<Project> result = filter.apply(projects, filterDto).toList();

        assertEquals(0, result.size());
    }


    private List<Project> getProjects() {
        return List.of(
                Project.builder().id(1L).name("Name LastName").build(),
                Project.builder().id(2L).name("Empty").build(),
                Project.builder().id(3L).name("Not empty").build(),
                Project.builder().id(4L).name("Not blank").build(),
                Project.builder().id(5L).name("  Test  ").build(),
                Project.builder().id(6L).name(" some test ").build(),
                Project.builder().id(7L).name("too some test").build(),
                Project.builder().id(8L).name("Name").build()
        );
    }
}