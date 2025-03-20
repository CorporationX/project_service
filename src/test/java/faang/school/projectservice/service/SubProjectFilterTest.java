package faang.school.projectservice.service;

import faang.school.projectservice.dto.SubProjectsFilterDto;
import faang.school.projectservice.filter.subproject.SubProjectNameFilter;
import faang.school.projectservice.filter.subproject.SubProjectStatusFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubProjectFilterTest {

    private final SubProjectNameFilter nameFilter = new SubProjectNameFilter();
    private final SubProjectStatusFilter statusFilter = new SubProjectStatusFilter();

    @Test
    void testIsApplicableWhenNameIsNotNull() {
        SubProjectsFilterDto filterDto = createFilter("test", ProjectStatus.COMPLETED);
        assertTrue(nameFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenNameIsNull() {
        SubProjectsFilterDto filterDto = createFilter(null, ProjectStatus.COMPLETED);
        assertFalse(nameFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenStatusIsNotNull() {
        SubProjectsFilterDto filterDto = createFilter("test", ProjectStatus.COMPLETED);
        assertTrue(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenStatusIsNull() {
        SubProjectsFilterDto filterDto = createFilter("test", null);
        assertFalse(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterByName() {
        SubProjectsFilterDto filterDto = createFilter("test", ProjectStatus.COMPLETED);
        Stream<Project> projects = Stream.of(
                createProject("test", ProjectStatus.CREATED, ProjectVisibility.PUBLIC),
                createProject("test2", ProjectStatus.CREATED, ProjectVisibility.PUBLIC)
        );
        List<Project> result = nameFilter.apply(projects, filterDto).toList();

        assertEquals(1, result.size());
    }

    @Test
    void testApplyFilterByNameNoResult() {
        SubProjectsFilterDto filterDto = createFilter("test", ProjectStatus.COMPLETED);
        Stream<Project> projects = Stream.of(
                createProject("test1", ProjectStatus.CREATED, ProjectVisibility.PUBLIC),
                createProject("test2", ProjectStatus.CREATED, ProjectVisibility.PUBLIC)
        );
        List<Project> result = nameFilter.apply(projects, filterDto).toList();

        assertEquals(0, result.size());
    }

    @Test
    void testApplyFilterByStatus() {
        SubProjectsFilterDto filterDto = createFilter("test", ProjectStatus.COMPLETED);
        Stream<Project> projects = Stream.of(
                createProject("test", ProjectStatus.COMPLETED, ProjectVisibility.PUBLIC),
                createProject("test2", ProjectStatus.CREATED, ProjectVisibility.PUBLIC)
        );
        List<Project> result = statusFilter.apply(projects, filterDto).toList();

        assertEquals(1, result.size());
    }

    @Test
    void testApplyFilterByStatusNoResult() {
        SubProjectsFilterDto filterDto = createFilter("test", ProjectStatus.COMPLETED);
        Stream<Project> projects = Stream.of(
                createProject("test", ProjectStatus.CREATED, ProjectVisibility.PUBLIC),
                createProject("test2", ProjectStatus.CREATED, ProjectVisibility.PUBLIC)
        );
        List<Project> result = statusFilter.apply(projects, filterDto).toList();

        assertEquals(0, result.size());
    }

    private SubProjectsFilterDto createFilter(String name, ProjectStatus status) {
        return new SubProjectsFilterDto(1L, name, status);
    }

    private Project createProject(String name, ProjectStatus status, ProjectVisibility visibility) {
        return Project.builder()
                .name(name)
                .status(status)
                .visibility(visibility)
                .build();
    }
}
