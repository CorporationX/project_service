package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private SubProjectController subProjectController;

    @Test
    void createSubProjectInvokesCreateSubProject() {
        SubProjectDto subProjectDto = SubProjectDto.builder()
                .name("Faang")
                .parentProjectId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .ownerId(2L)
                .build();

        subProjectController.createSubProject(subProjectDto);

        Mockito.verify(projectService).createSubProject(subProjectDto);
    }

    @Test
    void updateSubProjectInvokesUpdateSubProject() {
        UpdateSubProjectDto updateSubProjectDto = UpdateSubProjectDto.builder().build();

        subProjectController.updateSubProject(updateSubProjectDto);

        Mockito.verify(projectService).updateSubProject(updateSubProjectDto);
    }

    @Test
    void getProjectChildrenWithFilterInvokesGetProjectChildrenWithFilter() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().build();
        long id = 3;

        subProjectController.getProjectChildrenWithFilter(projectFilterDto, id);

        Mockito.verify(projectService).getProjectChildrenWithFilter(projectFilterDto, id);
    }
}