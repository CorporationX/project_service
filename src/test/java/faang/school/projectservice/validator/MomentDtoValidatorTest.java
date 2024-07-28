package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MomentDtoValidatorTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private MomentDtoValidator validator;

    private MomentDto dto;

    @BeforeEach
    public void setUp() {
        dto = new MomentDto();
    }

    @Test
    public void validateNullNameTest() {
        dto.setName(null);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentDo(dto));
    }

    @Test
    public void validateEmptyNameTest() {
        dto.setName("");
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentDo(dto));
    }

    @Test
    public void validateNullIdTest() {
        dto.setId(null);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentDo(dto));
    }

    @Test
    public void validateNegativeIdTest() {
        dto.setId(-1L);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentDo(dto));
    }

    @Test
    public void validateIfProjectsAreEmptyTest() {
        dto.setName("name");
        dto.setId(1L);
        dto.setProjectIds(List.of());
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentDo(dto));
    }

    @Test
    public void validateIfProjectsAreNotEmptyTest() {
        Project project = Project.builder().id(1L).name("project").build();
        dto.setName("name");
        dto.setId(1L);
        dto.setProjectIds(List.of(1L));
        Assertions.assertDoesNotThrow(()-> validator.validateMomentDo(dto));
    }

    @Test
    public void validateProjectIdsTest() {
        Project project = Project.builder().id(1L).name("project").status(ProjectStatus.CANCELLED).build();
        dto.setName("name");
        dto.setId(1L);
        dto.setProjectIds(List.of(1L));
        Mockito.when(projectRepository.existsById(1L)).thenReturn(false);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateProjectIds(dto));
    }

    @Test
    public void validateProjectIsCancelledTest() {
        Project project = Project.builder().id(1L).name("project").status(ProjectStatus.CANCELLED).build();
        dto.setName("name");
        dto.setId(1L);
        dto.setProjectIds(List.of(1L));
        Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateProjectIds(dto));
    }

    @Test
    public void validateProjectIsCompletedTest() {
        Project project = Project.builder().id(1L).name("project").status(ProjectStatus.COMPLETED).build();
        dto.setName("name");
        dto.setId(1L);
        dto.setProjectIds(List.of(1L));
        Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateProjectIds(dto));
    }

    @Test
    public void validateProjectIdTest() {
        Project project = Project.builder().id(1L).name("project").status(ProjectStatus.IN_PROGRESS).build();
        dto.setName("name");
        dto.setId(1L);
        dto.setProjectIds(List.of(1L));
        Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);
        Assertions.assertDoesNotThrow(()-> validator.validateProjectIds(dto));
    }
}
