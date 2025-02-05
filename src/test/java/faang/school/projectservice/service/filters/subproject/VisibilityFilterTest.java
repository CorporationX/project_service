package faang.school.projectservice.service.filters.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class VisibilityFilterTest {
    @Test
    void isApplicable() {
        SubProjectFilter filter = new VisibilityFilter();

        SubProjectFilterDto subProjectFilterDto = SubProjectFilterDto.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Assertions.assertTrue(filter.isApplicable(subProjectFilterDto));
    }

    @Test
    void isApplicableFalse() {
        SubProjectFilter filter = new VisibilityFilter();

        SubProjectFilterDto subProjectFilterDto = SubProjectFilterDto.builder()
                .name("test")
                .build();

        Assertions.assertFalse(filter.isApplicable(subProjectFilterDto));
    }

    @Test
    void apply() {
        SubProjectFilter filter = new VisibilityFilter();

        SubProjectFilterDto subProjectFilterDto = SubProjectFilterDto.builder()
                .name("test")
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Stream<Project> projectStream = Stream.of(
                Project.builder()
                        .id(1L)
                        .status(ProjectStatus.COMPLETED)
                        .visibility(ProjectVisibility.PUBLIC)
                        .build(),
                Project.builder()
                        .id(2L)
                        .status(ProjectStatus.CREATED)
                        .visibility(ProjectVisibility.PRIVATE)
                        .build(),
                Project.builder()
                        .id(3L)
                        .status(ProjectStatus.COMPLETED)
                        .visibility(ProjectVisibility.PUBLIC)
                        .build()
        );

        List<Project> expected = List.of(
                Project.builder()
                        .id(1L)
                        .status(ProjectStatus.COMPLETED)
                        .visibility(ProjectVisibility.PUBLIC)
                        .build(),
                Project.builder()
                        .id(3L)
                        .status(ProjectStatus.COMPLETED)
                        .visibility(ProjectVisibility.PUBLIC)
                        .build()
        );

        List<Project> actual = filter.apply(projectStream, subProjectFilterDto).toList();
        Assertions.assertEquals(expected, actual);
    }
}