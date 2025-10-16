package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ProjectServiceImplTest — описание класса.
 * <p>
 * Тесты методов создания проекта и его валидации в сервисном классе
 * </p>*
 *
 * @author fuckmynameagain
 * @since 21.08.2025
 */
public class ProjectServiceImplTest {
    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final ProjectMapper projectMapper = mock(ProjectMapper.class);
    private final List<ProjectFilter> filters = List.of();
    private final UserContext userContext = mock(UserContext.class);


    private final ProjectServiceImpl service =
            new ProjectServiceImpl(projectRepository, projectMapper, filters, userContext);

    @Test
    void testCreateProject() {
        ProjectDto inputDto = new ProjectDto();
        inputDto.setName("My Project");
        inputDto.setOwnerId(10L);

        Project project = new Project();
        project.setName("My Project");
        project.setOwnerId(10L);

        when(projectMapper.toEntity(inputDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(inputDto);

        ProjectDto result = service.createProject(inputDto);

        assertNotNull(result);
        assertEquals("My Project", result.getName());
        assertEquals(10L, result.getOwnerId());
    }

    @Test
    void testCreateProjectWithoutName() {
        ProjectDto dto = new ProjectDto();
        dto.setOwnerId(1L);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createProject(dto));

        assertEquals("Название проекта не может быть пустым", exception.getMessage());
    }

    @Test
    void testCreateProjectWithoutOwner() {
        ProjectDto dto = new ProjectDto();
        dto.setName("My Project");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createProject(dto));

        assertEquals("Не указан владелец проекта", exception.getMessage());
    }

    @Test
    void testCreateProjectAlreadyExists() {
        ProjectDto dto = new ProjectDto();
        dto.setName("My Project");
        dto.setOwnerId(1L);

        when(projectRepository.existsByOwnerIdAndName(1L, "My Project"))
                .thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createProject(dto));

        assertEquals("Проект с таким именем уже существует", exception.getMessage());
    }
}