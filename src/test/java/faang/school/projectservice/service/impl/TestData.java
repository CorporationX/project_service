package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestData {
    static List<Project> getSomeActiveProjects() {
        List<Project> activeProjects = new ArrayList<>();

        Project project1 = Project.builder().id(12L).name("Project 1").status(ProjectStatus.IN_PROGRESS).build();
        Project project2 = Project.builder().id(13L).name("Project 2").status(ProjectStatus.IN_PROGRESS).build();
        Project project3 = Project.builder().id(14L).name("Project 3").status(ProjectStatus.IN_PROGRESS).build();

        activeProjects.add(project1);
        activeProjects.add(project2);
        activeProjects.add(project3);

        return activeProjects;
    }

    static List<Project> getSomeActiveProjects2() {
        List<Project> activeProjects = new ArrayList<>();

        Project project1 = Project.builder().id(42L).name("Project 42").status(ProjectStatus.IN_PROGRESS).build();
        Project project2 = Project.builder().id(43L).name("Project 43").status(ProjectStatus.IN_PROGRESS).build();

        activeProjects.add(project1);
        activeProjects.add(project2);

        return activeProjects;
    }
    static List<Project> getSomeNotActiveProjects() {
        List<Project> activeProjects = new ArrayList<>();

        Project project1 = Project.builder().id(22L).name("Project 11").status(ProjectStatus.CREATED).build();
        Project project2 = Project.builder().id(23L).name("Project 12").status(ProjectStatus.CANCELLED).build();
        Project project3 = Project.builder().id(24L).name("Project 13").status(ProjectStatus.COMPLETED).build();

        activeProjects.add(project1);
        activeProjects.add(project2);
        activeProjects.add(project3);

        return activeProjects;
    }

    static List<Moment> getSomeMoments() {
        List<Moment> moments = new ArrayList<>();
        Moment moment1 = Moment.builder()
                .id(1L)
                .name("moment 1")
                .date(LocalDateTime.now())
                .projects(getSomeNotActiveProjects())
                .build();
        Moment moment2 = Moment.builder()
                .id(2L)
                .name("moment 2")
                .date(LocalDateTime.MIN)
                .projects(getSomeActiveProjects())
                .build();
        Moment moment3 = Moment.builder()
                .id(3L)
                .name("moment 3")
                .date(LocalDateTime.MAX)
                .projects(getSomeActiveProjects2())
                .build();
        moments.add(moment1);
        moments.add(moment2);
        moments.add(moment3);

        return moments;
    }

}
