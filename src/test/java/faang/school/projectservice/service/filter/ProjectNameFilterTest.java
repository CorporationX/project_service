package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectNameFilterTest {

    private final ProjectNameFilter projectNameFilter = new ProjectNameFilter();
    private final ProjectFilterDto projectFilterDto = new ProjectFilterDto();

    private static final String NAME = "name";

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Возвращает true если имя есть в фильтре")
        public void testIsApplicableWithName() {
            projectFilterDto.setName(NAME);
            boolean result = projectNameFilter.isApplicable(projectFilterDto);

            assertTrue(result);
        }

        @Test
        @DisplayName("Возвращает список сущностей если имя совпадает с именем в фильтре")
        public void testApplyFilterByNameWithNameInFilter() {
            Project first = new Project();
            first.setName(NAME);
            Project second = new Project();
            second.setName(NAME + 1);
            projectFilterDto.setName(NAME);

            Stream<Project> projectStream = Stream.of(first, second);
            List<Project> projects = projectNameFilter.apply(projectStream, projectFilterDto).toList();

            assertEquals(1, projects.size());
            assertEquals(first, projects.get(0));
        }

        @Test
        @DisplayName("Возвращает пустой список если имя не совпадает с именем в фильтре")
        public void testApplyFilterByNameWithNameNotExist() {
            Project first = new Project();
            first.setName(NAME + 1);
            Project second = new Project();
            second.setName(NAME + 2);
            projectFilterDto.setName(NAME);

            Stream<Project> projectStream = Stream.of(first, second);
            List<Project> projects = projectNameFilter.apply(projectStream, projectFilterDto).toList();

            assertEquals(0, projects.size());
        }
    }

    @Nested
    class NegativeTest {

        @Test
        @DisplayName("Возвращает false если имя отсутствует в фильтре")
        public void testIsApplicableWithoutNameInFilter() {
            boolean result = projectNameFilter.isApplicable(projectFilterDto);

            assertFalse(result);
        }
    }
}