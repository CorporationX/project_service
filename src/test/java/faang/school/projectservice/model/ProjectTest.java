package faang.school.projectservice.model;

import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectVisibility;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectTest {

    @Test
    public void testSetPrivateVisibility() {
        // arrange
        List<Project> children = getChildrenList(ProjectVisibility.PUBLIC);

        Project project = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .children(children)
                .build();

        List<Project> expected = getChildrenList(ProjectVisibility.PRIVATE);

        // act
        project.setPrivateVisibility();
        List<Project> actual = project.getChildren();

        // assert
        assertEquals(expected, actual);
    }

    private List<Project> getChildrenList(ProjectVisibility visibility) {
        Project firstProject = Project.builder()
                .visibility(visibility)
                .build();
        Project secondProject = Project.builder()
                .visibility(visibility)
                .build();
        Project thirdProject = Project.builder()
                .visibility(visibility)
                .build();

        Project firstProjectFirstChild = Project.builder()
                .visibility(visibility)
                .build();
        Project firstProjectSecondChild = Project.builder()
                .visibility(visibility)
                .build();
        List<Project> firstProjectChildren = List.of(firstProjectFirstChild,
                firstProjectSecondChild
        );

        firstProject.setChildren(firstProjectChildren);

        return List.of(firstProject, secondProject, thirdProject);
    }
}
