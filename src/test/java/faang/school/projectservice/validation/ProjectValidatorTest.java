package faang.school.projectservice.validation;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {
    @InjectMocks
    private ProjectValidator projectValidator;
    @Mock
    private ProjectRepository projectRepository;
    private ProjectDto firstProjectDto;

    @BeforeEach
    public void init() {
        firstProjectDto = new ProjectDto();
        firstProjectDto.setId(1L);
        firstProjectDto.setName("First project");
    }

    @Test
    public void testValidateProjectByOwnerIdAndNameOfProjectWithException() {
        when(projectRepository.existsByOwnerUserIdAndName(firstProjectDto.getOwnerId(), firstProjectDto.getName())).thenReturn(true);
        var exception = assertThrows(DataValidationException.class, () -> projectValidator.validateProjectByOwnerIdAndNameOfProject(firstProjectDto));
        assertEquals("The user with id: "+firstProjectDto.getOwnerId()+ " already has a project with name "+firstProjectDto.getName(), exception.getMessage());
    }
}
