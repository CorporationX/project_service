package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubProjectFilterStatusTest {
    private final SubProjectFilterStatus subProjectFilterStatus = new SubProjectFilterStatus();
    List<Project> projects = new ArrayList<>();
    private SubprojectFilterDto filterProgress;
    private SubprojectFilterDto filterCompleted;

    Project projectA = Project.builder()
            .status(ProjectStatus.IN_PROGRESS)
            .build();
    Project projectB = Project.builder()
            .status(ProjectStatus.COMPLETED)
            .build();
    Project projectC = Project.builder()
            .status(ProjectStatus.IN_PROGRESS)
            .build();

    @BeforeEach
    public void setUp() {
        projects.add(projectA);
        projects.add(projectB);
        projects.add(projectC);

        filterProgress = SubprojectFilterDto.builder()
                .statusFilter(ProjectStatus.IN_PROGRESS)
                .build();
        filterCompleted = SubprojectFilterDto.builder()
                .statusFilter(ProjectStatus.COMPLETED)
                .build();
    }

    @Test
    void testIsApplicable() {
        assertTrue(new SubProjectFilterStatus().isApplicable(filterProgress));
        assertTrue(new SubProjectFilterStatus().isApplicable(filterCompleted));
    }

    @Test
    void testApply() {
        assertEquals(2, subProjectFilterStatus.apply(projects.stream(), filterProgress).count());
        assertEquals(1, subProjectFilterStatus.apply(projects.stream(), filterCompleted).count());
    }
}