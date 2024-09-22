package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectStatusFilterTest {

    private final ProjectStatusFilter projectStatusFilter = new ProjectStatusFilter();
    private final ProjectFilterDto projectFilterDto = new ProjectFilterDto();
    private final Project first = new Project();
    private final Project second = new Project();

    @Nested
    class PositiveTests {

        @BeforeEach
        void init() {
            first.setStatus(ProjectStatus.IN_PROGRESS);
            second.setStatus(ProjectStatus.CREATED);
        }

        @Test
        @DisplayName("Возвращает true если статус есть в фильтре")
        public void whenIsApplicableWithStatusThenReturnTrue() {
            projectFilterDto.setStatus(ProjectStatus.IN_PROGRESS);

            boolean result = projectStatusFilter.isApplicable(projectFilterDto);

            assertTrue(result);
        }

        @Test
        @DisplayName("Возвращает список сущностей если статус совпадает со статусом в фильтре")
        public void whenApplyFilterWithExistedStatusThenSuccess() {
            projectFilterDto.setStatus(ProjectStatus.IN_PROGRESS);

            Stream<Project> projectStream = Stream.of(first, second);
            List<Project> projects = projectStatusFilter.apply(projectStream, projectFilterDto).toList();

            assertEquals(1, projects.size());
            assertEquals(first, projects.get(0));
        }

        @Test
        @DisplayName("Возвращает пустой список если статус не совпадает со статусом в фильтре")
        public void whenApplyFilterWithStatusNotExistThenSuccess() {
            projectFilterDto.setStatus(ProjectStatus.COMPLETED);

            Stream<Project> projectStream = Stream.of(first, second);
            List<Project> projects = projectStatusFilter.apply(projectStream, projectFilterDto).toList();

            assertEquals(0, projects.size());
        }
    }

    @Nested
    class NegativeTest {

        @Test
        @DisplayName("Возвращает false если статус отсутствует в фильтре")
        public void whenIsApplicableWithoutStatusThenReturnFalse() {
            boolean result = projectStatusFilter.isApplicable(projectFilterDto);

            assertFalse(result);
        }
    }
}