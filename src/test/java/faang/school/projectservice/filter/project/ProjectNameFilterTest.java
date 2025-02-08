package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.fillters.project.impl.ProjectNameFilter;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectNameFilterTest {
    private final ProjectNameFilter filter = new ProjectNameFilter();
    private ProjectFilterDto filterDto;

    private Project project1;
    private Project project2;

    Stream<Project> stream;

    @BeforeEach
    public void init() {
        filterDto = new ProjectFilterDto();

        project1 = Project.builder().name("Test Project 1").build();
        project2 = Project.builder().name("Another Project").build();

        stream = Stream.of(project1, project2);
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setNamePattern("Test");

        List<Project> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(project1, actual.get(0));
    }

    @Test
    public void testApplyCaseWithNotFullString() {
        filterDto.setNamePattern("Pro");

        List<Project> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithNamePatternNull() {
        List<Project> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithBlankString() {
        filterDto.setNamePattern("");

        List<Project> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithNoMatch() {
        filterDto.setNamePattern("Nonexistent");

        List<Project> actual = filter.apply(stream, filterDto).toList();

        assertEquals(0, actual.size());
    }
}

