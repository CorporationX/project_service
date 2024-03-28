package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectStatus.IN_PROGRESS;

public final class CreatingTestData {
    public static Stream<Project> createProjectsForTest() {
        return Stream.of(Project.builder()
                .name("subproject of the main improvement project")
                .status(CREATED)
                .build(), Project.builder()
                .name("improvement project")
                .status(IN_PROGRESS)
                .build());
    }

    public static ProjectFilterDto createProjectFilterDtoForTest() {
        return ProjectFilterDto.builder().namePattern("subproject").statusPattern(CREATED).build();
    }
}
