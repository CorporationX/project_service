package faang.school.projectservice.service.subproject;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.List;

public class Tree {
    Project projectD = Project.builder()
            .name("Project D")
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    Project projectE = Project.builder()
            .name("Project E")
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    Project projectF = Project.builder()
            .name("Project F")
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    Project projectG = Project.builder()
            .name("Project G")
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    Project projectB = Project.builder()
            .name("Project B")
            .visibility(ProjectVisibility.PUBLIC)
            .children(List.of(projectD, projectE))
            .build();
    Project projectC = Project.builder()
            .name("Project C")
            .visibility(ProjectVisibility.PUBLIC)
            .children(List.of(projectF, projectG))
            .build();

    Project parentProjectA = Project.builder()
            .name("parent Project A")
            .visibility(ProjectVisibility.PUBLIC)
            .build();
    Project projectA = Project.builder()
            .name("Project A")
            .visibility(ProjectVisibility.PUBLIC)
            .children(List.of(projectB, projectC))
            .parentProject(parentProjectA)
            .build();
}
