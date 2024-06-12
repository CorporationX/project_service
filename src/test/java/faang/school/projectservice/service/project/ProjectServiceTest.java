package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.testData.project.ProjectTestData;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//TODO:закончить тесты
@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private ProjectMapper projectMapper;
    private ProjectTestData projectTestData;
    private Project project;

    @BeforeEach
    void setUp() {
        projectTestData = new ProjectTestData();
        project = projectTestData.getProject();
    }

    @Nested
    class PositiveTests {
        @Nested
        class SubprojectsTests {
            private Project subproject;
            private ProjectDto subprojectDto;

            @BeforeEach
            void setUp() {
                subproject = projectTestData.getSubproject();
                subprojectDto = projectTestData.getSubprojectDto();
            }

            @DisplayName("should save subproject when projectDto has parentId")
            @Test
            void createSubprojectTest() {
                when(projectMapper.toModel(any(ProjectDto.class))).thenReturn(subproject);
                when(projectRepository.getProjectById(anyLong())).thenReturn(project);
                when(projectRepository.save(any(Project.class))).thenReturn(subproject);
                when(projectMapper.toDto(any(Project.class))).thenReturn(subprojectDto);

                projectService.create(subprojectDto);

                verify(projectRepository).save(any(Project.class));
            }
        }


    }

    @Nested
    class NegativeTests {

    }
}
