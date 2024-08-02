package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {
    @InjectMocks
    private ProjectValidator projectValidator;
    @Mock
    private ProjectRepository projectRepository;

    private ProjectDto projectDto = ProjectDto.builder()
            .ownerId(1L)
            .name("Some name").build();

    @Test
    void validateProjectByOwnerWithNameOfProjectTest() {
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(false);
        projectValidator.validateProjectByOwnerWithNameOfProject(projectDto);
        verify(projectRepository).existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
    }

    @Test
    void validateProjectWithNameOfProjectNotValidNameTest() {
        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> projectValidator.validateProjectByOwnerWithNameOfProject(projectDto));
        verify(projectRepository).existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
    }
}
