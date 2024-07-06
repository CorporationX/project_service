package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataProjectValidation;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {

    @InjectMocks
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;

    private ProjectDto projectDto;
    private ProjectFilterDto projectFilterDto;
    private Project project;
    private MultipartFile multipartFile;
    private Long projectId;

    @BeforeEach
    void setUp() {
        multipartFile = mock(MultipartFile.class);
        project = Project.builder().id(1L).coverImageId("coverImageId").build();
        projectId = 1L;
    }

    @Test
    public void testCheckIsNullProjectDto() {
        projectDto = null;
        assertThrows(DataProjectValidation.class, () -> projectValidator.checkIsNull(projectDto));
    }

    @Test
    public void testCheckIsNullProjectFilterDto() {
        projectFilterDto = null;
        assertThrows(DataProjectValidation.class, () -> projectValidator.checkIsNull(projectFilterDto));
    }

    @Test
    public void testCheckIsNullProjectId() {
        Long id = null;
        assertThrows(DataProjectValidation.class, () -> projectValidator.checkIsNull(id));

    }

    @Test
    public void testCheckExistProject() {
        projectDto = ProjectDto.builder().ownerId(1L).name("Name").description("Description").build();
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(true);
        assertThrows(DataProjectValidation.class, () -> projectValidator.checkExistProject(projectDto));
    }

    @Test
    public void testCheckMaxSize_ExceedsLimit() {
        when(multipartFile.getSize()).thenReturn(6_000_000L);

        assertThrows(DataValidationException.class, () -> {
            projectValidator.checkMaxSize(multipartFile);
        });

        verify(multipartFile, times(2)).getSize();
    }

    @Test
    public void testCheckMaxSize_WithinLimit() {
        when(multipartFile.getSize()).thenReturn(5_000_000L);

        projectValidator.checkMaxSize(multipartFile);

        verify(multipartFile, times(1)).getSize();
    }

    @Test
    public void testCheckProjectInDBProjectExists() {
        when(projectRepository.existsById(projectId)).thenReturn(true);

        projectValidator.checkProjectInDB(projectId);

        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    public void testCheckProjectInDBProjectNotFound() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            projectValidator.checkProjectInDB(projectId);
        });

        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    public void testCheckExistPicId_CoverImageIdExists() {
        projectValidator.checkExistPicId(project);
    }

    @Test
    public void testCheckExistPicId_CoverImageIdNotFound() {
        Project projectWithoutCoverImageId = Project.builder().id(1L).coverImageId(null).build();

        assertThrows(DataValidationException.class, () -> {
            projectValidator.checkExistPicId(projectWithoutCoverImageId);
        });
    }
}