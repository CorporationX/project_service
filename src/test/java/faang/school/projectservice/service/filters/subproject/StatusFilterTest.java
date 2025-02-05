package faang.school.projectservice.service.filters.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class StatusFilterTest {

    @Test
    void isApplicable() {
        StatusFilter statusFilter = new StatusFilter();

        SubProjectFilterDto subProjectFilterDto = SubProjectFilterDto.builder()
                .status(ProjectStatus.COMPLETED)
                .name("test")
                .build();

        Assertions.assertTrue(statusFilter.isApplicable(subProjectFilterDto));
    }

    @Test
    void isApplicableFalse() {
        StatusFilter statusFilter = new StatusFilter();

        SubProjectFilterDto subProjectFilterDto = SubProjectFilterDto.builder()
                .name("test")
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Assertions.assertFalse(statusFilter.isApplicable(subProjectFilterDto));
    }

    @Test
    void apply() {
        StatusFilter statusFilter = new StatusFilter();

        SubProjectFilterDto subProjectFilterDto = SubProjectFilterDto.builder()
                .name("test")
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Stream<Project> projectStream = Stream.of(
                Project.builder()
                        .id(1L)
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .id(2L)
                        .status(ProjectStatus.CREATED)
                        .build(),
                Project.builder()
                        .id(3L)
                        .status(ProjectStatus.COMPLETED)
                        .build()
        );

        List<Project> expected = List.of(
                Project.builder()
                        .id(1L)
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .id(3L)
                        .status(ProjectStatus.COMPLETED)
                        .build()
        );

        List<Project> actual = statusFilter.apply(projectStream, subProjectFilterDto).toList();
        Assertions.assertEquals(expected, actual);
    }
}