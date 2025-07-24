package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.sub_project.SubProjectCreateDto;
import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.dto.sub_project.SubProjectUpdateDto;
import faang.school.projectservice.dto.sub_project.SubProjectViewDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ProjectServiceImplTest — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 23.07.2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тест для проверки сервиса для взаимодействием с подпроектами")
public class ProjectServiceImplTest {

    @Mock
    private UserContext context;

    @Spy
    private SubProjectMapperImpl mapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentRepository momentRepository;

    @InjectMocks
    private ProjectServiceImpl service;

    @Test
    @DisplayName("Исключение, когда родительского проекта не нашлось в БД")
    public void shouldThrowException_WhenParentIsNotExist() {
        SubProjectCreateDto dto = new SubProjectCreateDto(
                1L, "Name", "desc", ProjectVisibility.PRIVATE, null
        );

        when(projectRepository.findById(dto.parentId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(dto));
    }

    @Test
    @DisplayName("Валидация: если видимость проекта-родителя PRIVATE, а проекта-потомка PUBLIC")
    public void shouldThrowException_WhenParentIsPrivateAndChildIsPublic() {
        Long parentId = 1L;
        SubProjectCreateDto dto = new SubProjectCreateDto(
                parentId, "Name", "desc", ProjectVisibility.PUBLIC, null
        );

        Project parent = Project.builder()
                .id(parentId)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        when(context.getUserId()).thenReturn(1L);
        when(projectRepository.findById(dto.parentId())).thenReturn(Optional.ofNullable(parent));

        assertThrows(DataValidationException.class, () -> service.create(dto));
    }


    @Test
    @DisplayName("")
    public void shouldThrowException_WhenProjectStatusIsNotCompleted() {
        Long parentId = 1L;
        SubProjectCreateDto dto = new SubProjectCreateDto(
                parentId, "Name", "desc", ProjectVisibility.PUBLIC, null
        );
        Project parent = Project.builder()
                .id(parentId)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.COMPLETED)
                .build();

        when(context.getUserId()).thenReturn(1L);
        when(projectRepository.findById(dto.parentId())).thenReturn(Optional.ofNullable(parent));

        assertThrows(ForbiddenException.class, () -> service.create(dto));
    }


    @Test
    @DisplayName("")
    public void shouldReturnDto_WhenInputIsValid() {
        Long parentId = 1L;
        SubProjectCreateDto dto = new SubProjectCreateDto(
                parentId, "Name", "desc", ProjectVisibility.PUBLIC, null
        );
        Project parent = Project.builder()
                .id(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();

        Project exceptedProject = Project.builder()
                .id(2L)
                .description(dto.description())
                .name(dto.name())
                .visibility(dto.visibility())
                .status(ProjectStatus.CREATED)
                .parentProject(parent)
                .build();

        SubProjectViewDto exceptedDto = new SubProjectViewDto(
                2L,
                dto.name(),
                dto.description(),
                dto.visibility(),
                exceptedProject.getStatus(),
                dto.parentId(),
                List.of(),
                null,
                null
        );

        when(context.getUserId()).thenReturn(1L);
        when(projectRepository.findById(dto.parentId())).thenReturn(Optional.ofNullable(parent));
        when(mapper.toEntity(dto)).thenReturn(exceptedProject);
        when(projectRepository.save(exceptedProject)).thenReturn(exceptedProject);

        SubProjectViewDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(exceptedDto, result);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(mapper, times(1)).toEntity(any(SubProjectCreateDto.class));
        verify(mapper, times(1)).toDto(any(Project.class));
    }

    @Test
    @DisplayName("")
    public void shouldThrowNotFound_WhenProjectIsNotExits() {
        Long id = 1L;

        when(projectRepository.findById(id)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.update(id, any(SubProjectUpdateDto.class))
        );
    }

    @Test
    @DisplayName("")
    public void shouldThrowForbidden_WhenProjectStatusIsCompleted() {
        Long id = 1L;

        Project project = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        when(projectRepository.findById(id)).thenReturn(Optional.ofNullable(project));

        assertThrows(ForbiddenException.class, () -> service.update(id, any(SubProjectUpdateDto.class)));
    }

    @Test
    @DisplayName("")
    public void shouldThrowDataValidation_WhenUpdateCompletedWithIncompleteChildren() {
        Long id = 1L;
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.COMPLETED, ProjectVisibility.PUBLIC);
        Project project = Project.builder()
                .id(id)
                .children(List.of(Project.builder()
                                .id(2L).status(ProjectStatus.COMPLETED)
                                .build(),
                        Project.builder()
                                .id(3L).status(ProjectStatus.CREATED)
                                .build()))
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.findById(id)).thenReturn(Optional.ofNullable(project));

        assertThrows(DataValidationException.class, () -> service.update(id, updateDto));
    }

    @Test
    @DisplayName("Создает Moment с участниками при завершении проекта")
    public void shouldCreateCompletionMoment_WhenAllSubProjectCompleted() {
        Long id = 1L;
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.COMPLETED, ProjectVisibility.PUBLIC);
        Project project = Project.builder()
                .id(id)
                .children(List.of(
                        Project.builder()
                                .id(2L).status(ProjectStatus.COMPLETED)
                                .build(),
                        Project.builder()
                                .id(3L).status(ProjectStatus.COMPLETED)
                                .build()))
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project parent = Project.builder()
                .id(10L)
                .teams(List.of(
                        Team.builder().teamMembers(List.of(
                                TeamMember.builder().id(1L).build(),
                                TeamMember.builder().id(2L).build(),
                                TeamMember.builder().id(3L).build()
                        )).build()
                ))
                .children(List.of(project))
                .build();

        project.setParentProject(parent);


        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(momentRepository.save(any())).thenAnswer(inv -> {
            Moment moment = inv.getArgument(0);
            moment.setId(1L);
            return moment;
        });

        service.update(id, updateDto);

        ArgumentCaptor<Moment> captor = ArgumentCaptor.forClass(Moment.class);
        verify(momentRepository, times(1)).save(captor.capture());

        Moment savedMoment = captor.getValue();
        assertEquals("Выполнены все подпроекты!", savedMoment.getDescription());
        assertEquals(parent, savedMoment.getProjects().get(0));
        assertThat(savedMoment.getUserIds()).containsExactly(1L, 2L, 3L);
        assertEquals(1, savedMoment.getProjects().size());
        verify(projectRepository, times(1)).save(parent);
    }

    @Test
    @DisplayName("")
    public void shouldThrowForbidden_WhenPrivateParentHavePublicChildren() {
        Long id = 1L;
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);
        Project project = Project.builder()
                .id(id)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Project parent = Project.builder()
                .id(10L)
                .visibility(ProjectVisibility.PRIVATE)
                .children(List.of(project))
                .build();

        project.setParentProject(parent);

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));

        assertThrows(ForbiddenException.class, () -> service.update(id, updateDto));
    }

    @Test
    @DisplayName("Изменяет видимость всех подпроектов на PRIVATE при изменении родителя на PRIVATE")
    public void shouldUpdateChildrenOnPublic_WhenParentBecamePrivate() {
        Long id = 1L;
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.COMPLETED, ProjectVisibility.PRIVATE);
        Project project = Project.builder()
                .id(id)
                .children(List.of(
                        Project.builder()
                                .id(2L).status(ProjectStatus.COMPLETED)
                                .visibility(ProjectVisibility.PRIVATE)
                                .build(),
                        Project.builder()
                                .id(3L).status(ProjectStatus.COMPLETED)
                                .visibility(ProjectVisibility.PUBLIC)
                                .build()))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));

        service.update(id, updateDto);

        boolean allChildArePrivate = project.getChildren().stream()
                        .allMatch(child -> child.getVisibility() == ProjectVisibility.PRIVATE);
        assertTrue(allChildArePrivate);
        assertEquals(ProjectVisibility.PRIVATE, project.getVisibility());
        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
    }

    @Test
    @DisplayName("Изменяет видимость только проекта-родителя на PUBLIC при обновлении на PUBLIC")
    public void shouldUpdateProjectOnPublic_WhenParentWasPrivate() {
        Long id = 1L;
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);
        Project parent = Project.builder()
                .id(id)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project child1 = Project.builder()
                .id(2L)
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Project child2 = Project.builder()
                .id(3L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        parent.setChildren(List.of(child1, child2));
        child2.setParentProject(parent);
        child1.setParentProject(parent);

        when(projectRepository.findById(id)).thenReturn(Optional.of(parent));

        service.update(id, updateDto);

        assertEquals(ProjectStatus.IN_PROGRESS, parent.getStatus());
        assertEquals(ProjectVisibility.PRIVATE, child1.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, child2.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, parent.getVisibility());
    }

    @Test
    @DisplayName("")
    public void shouldReturnViewDto_WhenUpdateSuccessful() {
        Long id = 1L;
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);
        Project parent = Project.builder()
                .id(id)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project child1 = Project.builder()
                .id(2L)
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Project child2 = Project.builder()
                .id(3L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        parent.setChildren(List.of(child1, child2));
        child2.setParentProject(parent);
        child1.setParentProject(parent);

        when(projectRepository.findById(id)).thenReturn(Optional.of(parent));
        when(projectRepository.save(parent)).thenReturn(parent);

        SubProjectViewDto result = service.update(id, updateDto);

        verify(projectRepository, times(1)).save(parent);
        verify(mapper, times(1)).toDto(parent);
        assertEquals(id, result.id());
        assertEquals(ProjectStatus.IN_PROGRESS, result.status());
        assertEquals(ProjectVisibility.PUBLIC, result.visibility());
        assertEquals(ProjectStatus.COMPLETED, child1.getStatus());
        assertEquals(ProjectStatus.IN_PROGRESS, child2.getStatus());
        assertEquals(ProjectVisibility.PRIVATE, child1.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, child2.getVisibility());
    }

    @Test
    @DisplayName("")
    public void shouldNotFound_WhenParentIsNotExist() {
        Long id = 1L;

        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getByFilter(id, new SubProjectFilterDto(null, null)));
    }


}