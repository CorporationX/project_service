package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.List;

public class Tree {
    Project parentProjectA = Project.builder()
            .id(0L)
            .name("parent Project A")
            .visibility(ProjectVisibility.PUBLIC)
            .build();

    Project projectAEntity = Project.builder()
            .name("Project A entity")
            .id(1L)
            .visibility(ProjectVisibility.PUBLIC)
            .parentProject(null)
            .build();
    ProjectDto projectA = ProjectDto.builder()
            .name("Project A")
            .id(1L)
            .visibility(ProjectVisibility.PUBLIC)
            .childrenIds(List.of(2L, 3L))
            .parentProjectId(null)
            .build();

    ProjectDto projectB = ProjectDto.builder()
            .name("Project B")
            .id(2L)
            .visibility(ProjectVisibility.PUBLIC)
            .childrenIds(List.of(4L, 5L))
            .build();
    ProjectDto projectC = ProjectDto.builder()
            .name("Project C")
            .id(3L)
            .visibility(ProjectVisibility.PUBLIC)
            .childrenIds(List.of(6L, 7L))
            .build();
    ProjectDto projectD = ProjectDto.builder()
            .name("Project D")
            .id(4L)
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    ProjectDto projectE = ProjectDto.builder()
            .name("Project E")
            .id(5L)
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    ProjectDto projectF = ProjectDto.builder()
            .name("Project F")
            .id(6L)
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    ProjectDto projectG = ProjectDto.builder()
            .name("Project G")
            .id(7L)
            .visibility(ProjectVisibility.PUBLIC)
            .build();

}

