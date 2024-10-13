package faang.school.projectservice.service.subproject.filters;

import faang.school.projectservice.filter.impl.SubProjectNameFilter;
import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@AllArgsConstructor
class SubProjectNameFilterTest {
    private SubProjectNameFilter filter;
    private ProjectDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubProjectNameFilter();
        filterDto = new ProjectDto();
    }

    @Test
    void isApplicableTrue() {
        filterDto.setName("test");
        assertDoesNotThrow(() -> filter.isApplicable(filterDto));
    }

    @Test
    void isApplicableFalse() {
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void apply() {
        List<Project> projects = IntStream.range(0, 3)
                .mapToObj(i -> new Project())
                .toList();
        projects.get(0).setName("test1");
        projects.get(1).setName("testName");
        projects.get(2).setName("testName2");
        filterDto.setName("testName");

        List<Project> filteredProjects = filter.apply(projects.stream(), filterDto).toList();

        assertAll(
                () -> assertEquals(2, filteredProjects.size()),
                () -> assertEquals(filterDto.getName(), filteredProjects.get(0).getName())
        );
    }
}