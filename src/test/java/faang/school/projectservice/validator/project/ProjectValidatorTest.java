//package faang.school.projectservice.validator.project;
//
//import faang.school.projectservice.model.Project;
//import faang.school.projectservice.model.ProjectStatus;
//import faang.school.projectservice.repository.ProjectRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static reactor.core.publisher.Mono.when;
//
//@ExtendWith(MockitoExtension.class)
//public class ProjectValidatorTest {
//    @Mock
//    ProjectRepository projectRepository;
//    @InjectMocks
//    ProjectValidator projectValidator;
//    private Project project1;
//    private Project project2;
//    @Test
//    public void TestValidatorOpenProject(){
//        project1.setStatus(ProjectStatus.CANCELLED);
//        project1.setId(1L);
//        project2.setStatus(ProjectStatus.CANCELLED);
//        project1.setId(2L);
//        List<Long> projectsIds = Arrays.asList(1L,2L);
//        List<Project> projects = Arrays.asList(project1,project2);
//        //when(projectRepository.findAllByIds(projectsIds)).thenReturn(projects);
//
//        projectValidator.ValidatorOpenProject(projectsIds);
//
//        assertThrows(IllegalArgumentException.class,()->projectValidator.ValidatorOpenProject(projectsIds));
//    }
//}
