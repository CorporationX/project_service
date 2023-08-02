package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
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
    private CreateSubProjectDto createSubProjectDto = CreateSubProjectDto.builder().build();

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testCreateSubProject() {
        subProjectController.createSubProject(createSubProjectDto);

        Mockito.verify(subProjectValidator, Mockito.times(1))
                .validateCreateProjectDto(createSubProjectDto);
        Mockito.verify(subProjectService,Mockito.times(1))
                .createProject(createSubProjectDto);
    }
}