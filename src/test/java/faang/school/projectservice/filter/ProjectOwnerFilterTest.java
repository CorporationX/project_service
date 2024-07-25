package faang.school.projectservice.filter;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.filter.project.ProjectOwnerFilter;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectOwnerFilterTest {

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectOwnerFilter projectOwnerFilter;

    @Test
    void applyShouldReturnProjectsOwnedByUser() {
        var project1 = new Project();
        project1.setOwnerId(1L);
        var project2 = new Project();
        project2.setOwnerId(2L);
        var project3 = new Project();
        project3.setOwnerId(1L);

        when(userContext.getUserId()).thenReturn(1L);

        var result = projectOwnerFilter.apply(Stream.of(project1, project2, project3));

        assertEquals(2, result.count());
    }

}