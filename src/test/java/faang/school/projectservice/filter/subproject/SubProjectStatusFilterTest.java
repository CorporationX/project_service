package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SubProjectStatusFilterTest {
    private SubProjectStatusFilter filter;

    private Project firstProject;
    private Project secondProject;
    private Project thirdProject;
    private List<Project> projects;

    @BeforeEach
    public void setUp() {
        firstProject = Project.builder()
                .name("Josh")
                .status(ProjectStatus.CREATED)
                .build();
        secondProject = Project.builder()
                .name("John")
                .status(ProjectStatus.COMPLETED)
                .build();
        thirdProject = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        projects = List.of(firstProject, secondProject, thirdProject);

        filter = new SubProjectStatusFilter();
    }

    @Test
    public void testIsApplicable() {
        // arrange
        SubProjectFilterDto filterDto = SubProjectFilterDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        boolean expected = true;

        // act
        boolean actual = filter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testIsNotApplicable() {
        // arrange
        SubProjectFilterDto filterDto = SubProjectFilterDto.builder().build();
        boolean expected = false;

        // act
        boolean actual = filter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApply() {
        // arrange
        SubProjectFilterDto filterDto = SubProjectFilterDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        List<Project> expected = List.of(thirdProject);

        // act
        List<Project> actual = filter.apply(projects, filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApplyNoMatches() {
        // arrange
        Project firstProject = Project.builder()
                .name("Josh")
                .status(ProjectStatus.CREATED)
                .build();
        Project secondProject = Project.builder()
                .name("John")
                .status(ProjectStatus.COMPLETED)
                .build();
        Project thirdProject = Project.builder()
                .status(ProjectStatus.CANCELLED)
                .build();
        List<Project> projects = List.of(firstProject,
                secondProject,
                thirdProject);

        SubProjectFilterDto filterDto = SubProjectFilterDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        List<Project> expected = new ArrayList<>();

        // act
        List<Project> actual = filter.apply(projects, filterDto);

        // assert
        assertEquals(expected, actual);
    }

}
