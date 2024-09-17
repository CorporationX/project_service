package faang.school.projectservice.controller;

import faang.school.projectservice.controller.subproject.SubProjectController;
import faang.school.projectservice.service.subproject.SubProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {
    @Mock
   private SubProjectService subProjectService;

    @InjectMocks
    private SubProjectController controller;
    @Test
    void createSubProject() {
        assertThrows(
                NullPointerException.class,
                () ->  controller.createSubProject(null)
        );
    }
}