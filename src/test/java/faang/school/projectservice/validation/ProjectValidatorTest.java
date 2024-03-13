package faang.school.projectservice.validation;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectValidator projectValidator;

    @Test
    void validateProjectCreate_NullName_ThrowsException() {
        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectCreate(getProjectDtoNullName()));
    }

    @Test
    void validateProjectCreate_BlankName_ThrowsException() {
        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectCreate(getProjectDtoBlankName()));
    }

    @Test
    void validateProjectCreate_NullDescription_ThrowsException() {
        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectCreate(getProjectDtoNullDescription()));
    }

    @Test
    void validateProjectCreate_BlankDescription_ThrowsException() {
        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectCreate(getProjectDtoBlankDescription()));
    }

    @Test
    void validateProjectCreate_ProjectNameExists_ThrowsException() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(true);

        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectCreate(getValidProjectDto()));
        verify(projectRepository, times(1)).existsByOwnerUserIdAndName(anyLong(), anyString());
    }

    @Test
    void validateProjectCreate_EmptyVisibility_ThrowsException() {
        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectCreate(getProjectDtoNoVisibility()));
    }

    @Test
    void validateProjectUpdate_ProjectNotExists_ThrowsException() {
        when(projectRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, ()
                -> projectValidator.validateProjectUpdate(getProjectDtoNoVisibility()));
        verify(projectRepository, times(1)).existsById(anyLong());
    }

    @Test
    void validateProjectUpdate_NullName_ThrowsException() {
        when(projectRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectUpdate(getProjectDtoNullName()));
        verify(projectRepository, times(1)).existsById(anyLong());
    }

    @Test
    void validateProjectUpdate_BlankName_ThrowsException() {
        when(projectRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectUpdate(getProjectDtoBlankName()));
        verify(projectRepository, times(1)).existsById(anyLong());
    }

    @Test
    void validateProjectUpdate_NullDescription_ThrowsException() {
        when(projectRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectUpdate(getProjectDtoNullDescription()));
        verify(projectRepository, times(1)).existsById(anyLong());
    }

    @Test
    void validateProjectUpdate_BlankDescription_ThrowsException() {
        when(projectRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectUpdate(getProjectDtoBlankDescription()));
        verify(projectRepository, times(1)).existsById(anyLong());
    }

    @Test
    void validateProjectUpdate_EmptyVisibility_ThrowsException() {
        when(projectRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, ()
                -> projectValidator.validateProjectUpdate(getProjectDtoNoVisibility()));
        verify(projectRepository, times(1)).existsById(anyLong());
    }

    @Test
    void validateProjectUpdate_ValidEntity_DoesNotThrowException() {
        when(projectRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> projectValidator.validateProjectUpdate(getValidProjectDto()));
        verify(projectRepository, times(1)).existsById(anyLong());
    }

    @Test
    void validateProjectCreate_ValidEntity_DoesNotThrowException() {
        when(projectRepository.existsByOwnerUserIdAndName(anyLong(), anyString())).thenReturn(false);

        assertDoesNotThrow(() -> projectValidator.validateProjectCreate(getValidProjectDto()));
        verify(projectRepository, times(1)).existsByOwnerUserIdAndName(anyLong(), anyString());
    }

    private ProjectDto getValidProjectDto() {
        return ProjectDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private ProjectDto getProjectDtoNoVisibility() {
        return ProjectDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .build();
    }

    private ProjectDto getProjectDtoBlankDescription() {
        return ProjectDto.builder()
                .id(1L)
                .name("name")
                .description("   ")
                .build();
    }

    private ProjectDto getProjectDtoNullDescription() {
        return ProjectDto.builder()
                .id(1L)
                .name("name")
                .build();
    }

    private ProjectDto getProjectDtoBlankName() {
        return ProjectDto.builder()
                .id(1L)
                .name("   ")
                .build();
    }

    private ProjectDto getProjectDtoNullName() {
        return ProjectDto.builder()
                .id(1L)
                .build();
    }
}
