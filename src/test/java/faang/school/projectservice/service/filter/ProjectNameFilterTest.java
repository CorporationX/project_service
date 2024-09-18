package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProjectNameFilterTest {

    private final ProjectNameFilter projectNameFilter = new ProjectNameFilter();
    private final ProjectFilterDto projectFilterDto = new ProjectFilterDto();

    private static final String FIRST_NAME = "first";
    private static final String SECOND_NAME = "second";
    private static final String THIRD_NAME = "third";

    private final Project first = new Project();
    private final Project second = new Project();

    @Nested
    class PositiveTests {

        @BeforeEach
        void init() {
            first.setName(FIRST_NAME);
            second.setName(SECOND_NAME);
        }

        @Test
        @DisplayName("Возвращает true если имя есть в фильтре")
        public void whenIsApplicableWithNameThenReturnTrue() {
            projectFilterDto.setName(FIRST_NAME);
            boolean result = projectNameFilter.isApplicable(projectFilterDto);

            assertTrue(result);
        }

        @Test
        @DisplayName("Возвращает список сущностей если имя совпадает с именем в фильтре")
        public void whenApplyFilterWithNameThenSuccess() {
            projectFilterDto.setName(FIRST_NAME);

            Stream<Project> projectStream = Stream.of(first, second);
            List<Project> projects = projectNameFilter.apply(projectStream, projectFilterDto).toList();

            assertEquals(1, projects.size());
            assertEquals(first, projects.get(0));
        }

        @Test
        @DisplayName("Возвращает пустой список если имя не совпадает с именем в фильтре")
        public void whenApplyFilterWithoutNameThenSuccess() {
            projectFilterDto.setName(THIRD_NAME);

            Stream<Project> projectStream = Stream.of(first, second);
            List<Project> projects = projectNameFilter.apply(projectStream, projectFilterDto).toList();

            assertEquals(0, projects.size());
        }
    }

    @Nested
    class NegativeTest {

        @Test
        @DisplayName("Возвращает false если имя отсутствует в фильтре")
        public void whenIsApplicableWithoutNameThenReturnFalse() {
            boolean result = projectNameFilter.isApplicable(projectFilterDto);

            assertFalse(result);
        }
    }
}