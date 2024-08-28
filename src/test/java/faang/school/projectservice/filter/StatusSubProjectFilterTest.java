package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class StatusSubProjectFilterTest {

    private StatusSubProjectFilter filter;
    private SubProjectDtoFilter subProjectDtoFilter;
    private Project project1;
    private Project project2;

    @BeforeEach
    public void setUp() {
        filter = new StatusSubProjectFilter();
        subProjectDtoFilter = Mockito.mock(SubProjectDtoFilter.class);

        project1 = Project.builder()
                .id(1L)
                .name("Project One")
                .description("Description One")
                .storageSize(BigInteger.valueOf(100))
                .maxStorageSize(BigInteger.valueOf(200))
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        project2 = Project.builder()
                .id(2L)
                .name("Project Two")
                .description("Description Two")
                .storageSize(BigInteger.valueOf(150))
                .maxStorageSize(BigInteger.valueOf(250))
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
    }

    @Test
    public void testIsAcceptable_withNonNullStatus() {
        when(subProjectDtoFilter.getProjectStatus()).thenReturn(ProjectStatus.CREATED);
        assertTrue(filter.isAcceptable(subProjectDtoFilter));
    }

    @Test
    public void testIsAcceptable_withNullStatus() {
        when(subProjectDtoFilter.getProjectStatus()).thenReturn(null);
        assertFalse(filter.isAcceptable(subProjectDtoFilter));
    }

    @Test
    public void testApply_withMatchingStatus() {
        when(subProjectDtoFilter.getProjectStatus()).thenReturn(ProjectStatus.CREATED);
        Stream<Project> projectStream = Stream.of(project1, project2);
        Stream<Project> filteredStream = filter.apply(projectStream, subProjectDtoFilter);
        List<Project> filteredList = filteredStream.toList();
        assertEquals(1, filteredList.size());
        assertEquals(ProjectStatus.CREATED, filteredList.get(0).getStatus());
    }

    @Test
    public void testApply_withNonMatchingStatus() {
        when(subProjectDtoFilter.getProjectStatus()).thenReturn(ProjectStatus.COMPLETED);
        Stream<Project> projectStream = Stream.of(project1, project2);
        Stream<Project> filteredStream = filter.apply(projectStream, subProjectDtoFilter);
        List<Project> filteredList = filteredStream.toList();
        assertTrue(filteredList.isEmpty());
    }

    @Test
    public void testApply_withEmptyStream() {
        when(subProjectDtoFilter.getProjectStatus()).thenReturn(ProjectStatus.CREATED);
        Stream<Project> projectStream = Stream.empty();
        Stream<Project> filteredStream = filter.apply(projectStream, subProjectDtoFilter);
        List<Project> filteredList = filteredStream.toList();
        assertTrue(filteredList.isEmpty());
    }
}
