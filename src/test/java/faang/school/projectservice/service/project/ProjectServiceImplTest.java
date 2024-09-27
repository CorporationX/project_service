package faang.school.projectservice.service.project;

import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectValidator validator;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void testGetProjectOk() {
        projectService.getProject(1L);

        verify(validator).validateProject(anyLong());
    }
}
