package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.UpdateStatusSubprojectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class SubProjectControllerTest {
    @InjectMocks
    private SubProjectController subProjectController;
    @Mock
    private SubProjectService subProjectService;
    @Mock
    private SubProjectValidator subProjectValidator;
    private UpdateStatusSubprojectDto updateStatusSubprojectDto = UpdateStatusSubprojectDto.builder().build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSubProject() {
        subProjectController.updateStatusSubProject(updateStatusSubprojectDto);

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateUpdateStatusSubprojectDto(updateStatusSubprojectDto);
        Mockito.verify(subProjectService, Mockito.times(1))
                .updateStatusSubProject(updateStatusSubprojectDto);
    }
}