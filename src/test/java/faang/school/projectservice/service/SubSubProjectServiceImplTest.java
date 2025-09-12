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
import faang.school.projectservice.publisher.SubProjectCreatedEventPublisher;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.sub_project.SubProjectFilterServiceImpl;
import faang.school.projectservice.service.filter.sub_project.SubProjectNameFilter;
import faang.school.projectservice.service.filter.sub_project.SubProjectStatusFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ProjectServiceImplTest — класс для тестирования функционала
 * сервиса {@link SubProjectServiceImpl}
 *
 * @author Linempy
 * @since 23.07.2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тест для проверки сервиса для взаимодействием с подпроектами")
public class SubSubProjectServiceImplTest {

    @Mock
    private UserContext context;

    @Spy
    private SubProjectMapperImpl mapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SubProjectCreatedEventPublisher publisher;

    @Mock
    private MomentRepository momentRepository;

    private SubProjectServiceImpl service;

    @BeforeEach
    void setUp() {
        List<Filter<Project, SubProjectFilterDto>> realFilters = List.of(
                new SubProjectNameFilter(),
                new SubProjectStatusFilter()
        );
        SubProjectFilterServiceImpl filter = new SubProjectFilterServiceImpl(realFilters);
        filter = Mockito.spy(filter);

        service = new SubProjectServiceImpl(
                projectRepository,
                mapper,
                context,
                filter,
                momentRepository,
                publisher
        );
    }

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
    @DisplayName("Должен выбросить исключение, когда статус проекта-родителя COMPLETED")
    public void shouldThrowException_WhenParentStatusIsCompleted() {
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
    @DisplayName("Успешно вернуть данные при валидных данных")
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

        when(context.getUserId()).thenReturn(1L);
        when(projectRepository.findById(dto.parentId())).thenReturn(Optional.ofNullable(parent));
        when(mapper.toEntity(dto)).thenReturn(exceptedProject);
        when(projectRepository.save(exceptedProject)).thenReturn(exceptedProject);

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

        SubProjectViewDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(exceptedDto, result);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(mapper, times(1)).toEntity(any(SubProjectCreateDto.class));
        verify(mapper, times(1)).toViewDto(any(Project.class));
    }

    @Test
    @DisplayName("Выбросить исключение при обновлении несуществующего проекта")
    public void shouldThrowNotFound_WhenProjectIsNotExits() {
        Long id = 1L;

        when(projectRepository.findById(id)).thenThrow(NotFoundException.class);

        assertThrows(
                NotFoundException.class,
                () -> service.update(id, any(SubProjectUpdateDto.class))
        );
    }

    @Test
    @DisplayName("Выбросить исключение, когда статус обновляемого проекта COMPLETED")
    public void shouldThrowForbidden_WhenProjectStatusIsCompleted() {
        Long id = 1L;

        Project project = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        when(projectRepository.findById(id)).thenReturn(Optional.ofNullable(project));

        assertThrows(ForbiddenException.class, () -> service.update(id, any(SubProjectUpdateDto.class)));
    }

    @Test
    @DisplayName("Исключение, при обновляемом статусе COMPLETED, когда статусы у подпроектов не COMPLETED")
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
    @DisplayName("Создает Moment с участниками при завершении всех подпроектов")
    public void shouldCreateCompletionMoment_WhenAllSubProjectCompleted() {
        Long id = 1L;
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
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.COMPLETED, ProjectVisibility.PUBLIC);

        service.update(id, updateDto);

        ArgumentCaptor<Moment> captor = ArgumentCaptor.forClass(Moment.class);
        verify(momentRepository, times(1)).save(captor.capture());

        Moment savedMoment = captor.getValue();
        assertEquals("Выполнены все подпроекты!", savedMoment.getDescription());
        assertEquals(parent, savedMoment.getProjects().get(0));
        assertThat(savedMoment.getUserIds()).containsExactly(1L, 2L, 3L);
        assertEquals(1, savedMoment.getProjects().size());

        verify(projectRepository, times(1)).save(project);
    }

    @Test
    @DisplayName("Выбрасывает исключение при изменении приватного родителя на публичный с публичными детьми ")
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
        child2.setParentProject(parent);
        child1.setParentProject(parent);
        parent.setChildren(List.of(child1, child2));

        when(projectRepository.findById(id)).thenReturn(Optional.of(parent));
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);

        service.update(id, updateDto);

        assertEquals(ProjectStatus.IN_PROGRESS, parent.getStatus());
        assertEquals(ProjectVisibility.PRIVATE, child1.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, child2.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, parent.getVisibility());
    }

    @Test
    @DisplayName("Вернуть DTO при успешном обновлении проекта")
    public void shouldReturnViewDto_WhenUpdateSuccessful() {
        Long id = 1L;
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
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto(ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);

        SubProjectViewDto result = service.update(id, updateDto);

        verify(projectRepository, times(1)).save(parent);
        verify(mapper, times(1)).toViewDto(parent);
        assertEquals(id, result.id());
        assertEquals(ProjectStatus.IN_PROGRESS, result.status());
        assertEquals(ProjectVisibility.PUBLIC, result.visibility());
        assertEquals(ProjectStatus.COMPLETED, child1.getStatus());
        assertEquals(ProjectStatus.IN_PROGRESS, child2.getStatus());
        assertEquals(ProjectVisibility.PRIVATE, child1.getVisibility());
        assertEquals(ProjectVisibility.PUBLIC, child2.getVisibility());
    }

    @Test
    @DisplayName("Должен выбросить NotFoundException, когда родительский проект не существует")
    public void shouldNotFound_WhenParentIsNotExist() {
        Long id = 1L;

        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getByFilter(id, new SubProjectFilterDto(null, null)));
        verify(projectRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Фильтрация по имени")
    public void shouldReturnFilteredDtos_WhenFilterDtoByName() {
        Long parentId = 1L;
        SubProjectFilterDto filterDto = new SubProjectFilterDto("Name", null);
        Project child1 = createProject(2L, "Name1", ProjectStatus.COMPLETED, ProjectVisibility.PRIVATE);
        Project child2 = createProject(3L, "name2", ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);
        Project child3 = createProject(4L, "NON", ProjectStatus.CANCELLED, ProjectVisibility.PUBLIC);

        Project parent = createParentProject(parentId, "Name Name", List.of(child1, child2, child3));
        when(projectRepository.findById(parentId)).thenReturn(Optional.of(parent));

        List<SubProjectViewDto> result = service.getByFilter(parentId, filterDto);

        assertEquals(2, result.size());
        verify(mapper, times(result.size())).toViewDto(any(Project.class));
        verify(mapper, never()).toViewDto(child3);
    }

    @Test
    @DisplayName("Фильтрация по имени и статусу")
    public void shouldReturnFilteredDtos_WhenFilterDtoByNameAndStatus() {
        Long parentId = 1L;
        SubProjectFilterDto filterDto = new SubProjectFilterDto("Name", ProjectStatus.COMPLETED);
        Project child1 = createProject(2L, "Name1", ProjectStatus.COMPLETED, ProjectVisibility.PRIVATE);
        Project child2 = createProject(3L, "name2", ProjectStatus.IN_PROGRESS, ProjectVisibility.PUBLIC);
        Project child3 = createProject(4L, "NON", ProjectStatus.CANCELLED, ProjectVisibility.PUBLIC);
        Project parent = createParentProject(parentId, "name name", List.of(child1, child2, child3));
        SubProjectViewDto dto1 = new SubProjectViewDto(
                child1.getId(), child1.getName(), child1.getDescription(),
                child1.getVisibility(), child1.getStatus(), child1.getParentProject().getId(),
                List.of(), null, null
        );

        when(projectRepository.findById(parentId)).thenReturn(Optional.of(parent));

        List<SubProjectViewDto> result = service.getByFilter(parentId, filterDto);

        assertEquals(1, result.size());
        assertEquals(dto1, result.get(0));
        verify(mapper, times(1)).toViewDto(child1);
        verify(mapper, never()).toViewDto(child2);
        verify(mapper, never()).toViewDto(child3);
    }

    private Project createProject(Long id, String name, ProjectStatus status, ProjectVisibility visibility) {
        return Project.builder()
                .id(id)
                .name(name)
                .status(status)
                .visibility(visibility)
                .build();
    }

    private Project createParentProject(Long id, String name, List<Project> children) {
        Project parent = Project.builder()
                .id(id)
                .name(name)
                .status(ProjectStatus.CREATED)
                .children(children)
                .build();

        children.forEach(child -> child.setParentProject(parent));
        return parent;
    }
}