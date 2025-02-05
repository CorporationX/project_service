package faang.school.projectservice.service.filters.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class NameFilterTest {

    @Test
    void isApplicable() {
        NameFilter nameFilter = new NameFilter();

        SubProjectFilterDto subProjectFilterDto = SubProjectFilterDto.builder()
                .name("test")
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Assertions.assertTrue(nameFilter.isApplicable(subProjectFilterDto));
    }

    @Test
    void isApplicableFalse() {
        NameFilter nameFilter = new NameFilter();

        SubProjectFilterDto subProjectFilterDto = SubProjectFilterDto.builder()
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Assertions.assertFalse(nameFilter.isApplicable(subProjectFilterDto));
    }

    @Test
    void apply() {
        NameFilter nameFilter = new NameFilter();

        SubProjectFilterDto subProjectFilterDto = SubProjectFilterDto.builder()
                .name("test")
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Stream<Project> projectStream = Stream.of(
                Project.builder()
                        .id(1L)
                        .name("test-project")
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .id(2L)
                        .name("project")
                        .status(ProjectStatus.CREATED)
                        .build(),
                Project.builder()
                        .id(3L)
                        .name("project-test2")
                        .status(ProjectStatus.COMPLETED)
                        .build()
        );

        List<Project> expected = List.of(
                Project.builder()
                        .id(1L)
                        .name("test-project")
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .id(3L)
                        .name("project-test2")
                        .status(ProjectStatus.COMPLETED)
                        .build()
        );

        List<Project> actual = nameFilter.apply(projectStream, subProjectFilterDto).toList();
        Assertions.assertEquals(expected, actual);
    }
}