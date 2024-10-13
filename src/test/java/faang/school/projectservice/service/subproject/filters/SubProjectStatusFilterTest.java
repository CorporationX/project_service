package faang.school.projectservice.service.subproject.filters;

import faang.school.projectservice.filter.impl.SubProjectStatusFilter;
import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.enums.ProjectStatus;
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
class SubProjectStatusFilterTest {
    private SubProjectStatusFilter filter;
    private ProjectDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubProjectStatusFilter();
        filterDto = new ProjectDto();
    }

    @Test
    void isApplicableTrue() {
        filterDto.setStatus(ProjectStatus.COMPLETED);
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
        projects.get(0).setStatus(ProjectStatus.CANCELLED);
        projects.get(1).setStatus(ProjectStatus.CREATED);
        projects.get(2).setStatus(ProjectStatus.COMPLETED);
        filterDto.setStatus(ProjectStatus.COMPLETED);


        List<Project> filteredProjects = filter.apply(projects.stream(), filterDto).toList();

        assertAll(
                () -> assertEquals(1, filteredProjects.size()),
                () -> assertEquals(filterDto.getStatus(), filteredProjects.get(0).getStatus())
        );
    }
}