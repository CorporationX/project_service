package faang.school.projectservice.controller;

import faang.school.projectservice.dto.subprojectdto.SubProjectDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubProjectControllerTest {
    @Mock
    private ProjectService projectService;

    @InjectMocks
    private SubProjectController subProjectController;

    SubProjectDto subProjectDto;
    SubProjectFilterDto subProjectFilterDto;
    long projectId = 1L;

    @Test
    void testCreateSubProject() {
        subProjectController.createSubProject(subProjectDto);
        verify(projectService).createSubProject(subProjectDto);
    }

    @Test
    void testUpdateSubProject() {
        subProjectController.updateSubProject(subProjectDto);
        verify(projectService).updateSubProject(subProjectDto);
    }

    @Test
    void testGetAllFilteredSubprojectsOfAProject() {
        subProjectController.getAllFilteredSubprojectsOfAProject(subProjectFilterDto, projectId);
        verify(projectService).getAllFilteredSubprojectsOfAProject(subProjectFilterDto, projectId);
    }
}