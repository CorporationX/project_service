package faang.school.projectservice.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import faang.school.projectservice.model.Project;
//import faang.school.projectservice.repository.ProjectRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//public class ProjectServiceTest {
//    @InjectMocks
//    private ProjectService projectService;
//    @Mock
//    private ProjectRepository projectRepository;
//
//    @Test
//    void testProjectExistsIsInvalid() {
//        when(projectRepository.getProjectById(1L)).thenReturn(null);
//
//        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
//                () -> projectService.getProjectById(1L));
//        assertEquals(illegalArgumentException.getMessage(), "Project's id not found");
//    }
//
//    @Test
//    void testProjectIsSaved() {
//        Project project = new Project();
//        projectService.save(project);
//        verify(projectRepository, times(1)).save(project);
//    }
//}
