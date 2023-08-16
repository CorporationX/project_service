package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubProjectFilterNameTest {
    private final SubProjectFilterName subProjectFilterName = new SubProjectFilterName();
    List<Project> projects = new ArrayList<>();
    private SubprojectFilterDto filterA;
    private SubprojectFilterDto filterB;

    Project projectA = Project.builder()
            .name("Project A")
            .build();
    Project projectB = Project.builder()
            .name("Project B")
            .build();
    Project projectC = Project.builder()
            .name("Project B")
            .build();

    @BeforeEach
    public void setUp() {
        filterA = SubprojectFilterDto.builder()
                .nameFilter("Project A")
                .build();
        filterB = SubprojectFilterDto.builder()
                .nameFilter("Project B")
                .build();
    }
    @Test
    void testIsApplicable() {
        assertTrue(new SubProjectFilterName().isApplicable(filterA));
        assertTrue(new SubProjectFilterName().isApplicable(filterB));
    }

    @Test
    void testApply() {
        assertEquals(2, subProjectFilterName.apply(projects.stream(), filterB).count());
        assertEquals(1, subProjectFilterName.apply(projects.stream(), filterA).count());
    }
}