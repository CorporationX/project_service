package faang.school.projectservice.filter;

import faang.school.projectservice.filter.project.PublicProjectFilter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PublicProjectFilterTest {

    @InjectMocks
    private PublicProjectFilter publicProjectFilter;

    @Test
    void applyShouldReturnPublicProjects() {
        var project1 = new Project();
        project1.setVisibility(ProjectVisibility.PUBLIC);
        var project2 = new Project();
        project2.setVisibility(ProjectVisibility.PRIVATE);
        var project3 = new Project();
        project3.setVisibility(ProjectVisibility.PUBLIC);

        var result = publicProjectFilter.apply(Stream.of(project1, project2, project3));

        assertEquals(2, result.count());
    }

}