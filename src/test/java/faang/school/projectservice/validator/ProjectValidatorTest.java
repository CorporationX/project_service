package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exceptions.DataProjectValidation;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {

    @InjectMocks
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;

    private ProjectDto projectDto;
    private ProjectFilterDto projectFilterDto;

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
}