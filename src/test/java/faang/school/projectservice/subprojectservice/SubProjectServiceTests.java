package faang.school.projectservice.subprojectservice;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.subproject.SubProjectServiceImpl;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ChildrenNotFinishedException;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;
import faang.school.projectservice.validator.SubProjectValidator;
import faang.school.projectservice.validator.SubProjectValidatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class SubProjectServiceTests {
    @Mock
    private ProjectRepository repository;
    @Spy
    private ProjectMapperImpl mapper = new ProjectMapperImpl();
    @Spy
    private SubProjectValidator validator = new SubProjectValidatorImpl();
    @InjectMocks
    private SubProjectServiceImpl service;

    /**
     * если валидация прошла то должна сохранится в базу
     */
    @Test
    public void if_subproject_have_parent_id_repository_must_be_called(){
        Project rootParent = Project.builder().id(3L).build();
        Project parentProject = Project.builder().id(1L).visibility(ProjectVisibility.PUBLIC).build();
        parentProject.setParentProject(rootParent);
        Project subproject1 = Project.builder().parentProject(parentProject).visibility(ProjectVisibility.PUBLIC).build();

        when(repository.getProjectById(1L)).thenReturn(subproject1);

        service.createSubProject(1L);

        verify(repository).save(subproject1);

    }

    /**
     * При этом проверить, все подпроекты текущего подпроекта имеют тот же статус. Т.е. нельзя закрыть проект, если у
     * него есть все ещё открытые подпроекты. Если так то нужно выкинуть исключение ChildrenNotFinishedException
     */
    @Test
    public void shouldThrowChildrenNotFinishedException_WhenChildProjectsNotCompleted() {

        Project childProject1 = Project.builder()
                .id(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        Project childProject2 = Project.builder()
                .id(3L)
                .status(ProjectStatus.COMPLETED)
                .build();
         Project subProject = Project.builder()
                .id(1L)
                .status(ProjectStatus.COMPLETED)
                .children(List.of(childProject1, childProject2))
                .build();
        SubProjectDto subProjectDto = new SubProjectDto();
        subProjectDto.setId(1L);

        when(repository.getProjectById(subProjectDto.getId())).thenReturn(subProject);

        assertThrows(
                ChildrenNotFinishedException.class,
                () -> service.updateSubProject(subProjectDto)
        );

        verify(repository, times(1)).getProjectById(1L);
        verify(repository, times(0)).save(any(Project.class)); // Save should not be called
    }

    /**
     * При этом проверить, все подпроекты текущего подпроекта имеют тот же статус. Т.е. нельзя закрыть проект, если у
     * него есть все ещё открытые подпроекты. Нужно сначала закрывать все подпроекты, и только потом родительский проект
     */
    @Test
    public void shouldSaveProject_WhenAllChildProjectsAreCompleted() throws ChildrenNotFinishedException {

        Project childProject1 = Project.builder()
                .id(2L)
                .status(ProjectStatus.COMPLETED)
                .build();

        Project childProject2 = Project.builder()
                .id(3L)
                .status(ProjectStatus.COMPLETED)
                .build();
        Project subProject = Project.builder()
                .id(1L)
                .status(ProjectStatus.COMPLETED)
                .children(List.of(childProject1, childProject2))
                .build();

        SubProjectDto subProjectDto = new SubProjectDto();
        subProjectDto.setId(1L);

        when(repository.getProjectById(subProjectDto.getId())).thenReturn(subProject);
        when(repository.save(any(Project.class))).thenReturn(subProject);

        // Act
        service.updateSubProject(subProjectDto);

        // Assert
        verify(repository, times(1)).getProjectById(1L);
        verify(repository, times(1)).save(any(Project.class));
    }

    /**
     * Обновляется статус и для аудита проставляется TIMESTAMP на последнее изменение проекта.
     */
    @Test
    public void testSetStatusAndTime() {

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        LocalDateTime initialTime = LocalDateTime.now();

        Project result = service.getSetTime().apply(project);


        assertThat(result.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isEqualToIgnoringSeconds(initialTime);
    }

    /**
     * Проверить, и если у проекта закрылись все его подпроекты, тогда получаем Moment, а участники проекта становятся
     * участниками момента. Получается, что у проекта есть момент “Выполнены все подпроекты”.
     */
    @Test
    public void testAssignTeamMemberMoment() {
        TeamMember member1 = TeamMember.builder().id(1L).userId(101L).build();
        TeamMember member2 = TeamMember.builder().id(2L).userId(102L).build();

        Team team1 = Team.builder().teamMembers(List.of(member1)).build();
        Team team2 = Team.builder().teamMembers(List.of(member2)).build();

        Moment moment1 = new Moment();
        Moment moment2 = new Moment();

        Project project = new Project();
        project.setTeams(List.of(team1, team2));
        project.setMoments(List.of(moment1, moment2));

        Project result = service.getAssignTeamMemberMoment().apply(project);

        List<Long> expectedTeamMemberIds = List.of(1L, 2L);
        assertThat(result.getMoments()).hasSize(2);
        result.getMoments().forEach(moment -> {
            assertThat(moment.getUserIds()).containsExactlyInAnyOrderElementsOf(expectedTeamMemberIds);
        });
    }

    /**
     * функция setVisibility делает все приватным если нужно
     */
    @Test
    public void testSetVisibility_PrivateVisibility_ShouldSetSubprojectsToPrivate() {
        Project subProject1 = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        Project subProject2 = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        Project mainProject = Project.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .children(List.of(subProject1, subProject2))
                .build();

        Project resultProject = service.getSetVisibility().apply(mainProject);

        assertEquals(ProjectVisibility.PRIVATE, resultProject.getVisibility());
        resultProject.getChildren().forEach(subProject -> {
            assertEquals(ProjectVisibility.PRIVATE, subProject.getVisibility());
        });
    }

    /**
     * функция setVisibility не делает ничего если не нужно
     */
    @Test
    public void testSetVisibility_PublicVisibility_ShouldKeepVisibilityUnchanged() {
        Project subProject1 = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        Project subProject2 = Project.builder().visibility(ProjectVisibility.PUBLIC).build();
        Project mainProject = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .children(List.of(subProject1, subProject2))
                .build();

        Project resultProject = service.getSetVisibility().apply(mainProject);

        assertEquals(ProjectVisibility.PUBLIC, resultProject.getVisibility());
        resultProject.getChildren().forEach(subProject -> {
            assertEquals(ProjectVisibility.PUBLIC, subProject.getVisibility());
        });
    }

    /**
     * Подпроект ВСЕГДА имеет родительский проект.
     */
    @Test
    public void testCreateSubProject_ShouldThrowParentProjectMustNotBeNull_WhenParentProjectIsNull() {
        Project project = Project.builder().parentProject(null).build();

        when(repository.getProjectById(1L)).thenReturn(project);
        assertThrows(ParentProjectMusNotBeNull.class, () -> {
            service.createSubProject(1L);
        });

        verify(repository, never()).save(any(Project.class));
    }

    /**
     * Корневой проект не имеет родительского проекта./
     **/
    @Test
    public void testCreateSubProject_ShouldThrowRootProjectsParentMustNotBeNull_WhenParentHasAParent() {
        Project parentProject = Project.builder().id(1L).visibility(ProjectVisibility.PUBLIC).build();
        Project subproject1 = Project.builder().parentProject(parentProject).build();
        when(repository.getProjectById(1L)).thenReturn(subproject1);
        assertThrows(RootProjectsParentMustNotBeNull.class, () -> {
            service.createSubProject(1L);
        });

        verify(repository, never()).save(any(Project.class));
    }

    /**
     * Нельзя создать приватный подпроект для публичного родительского проекта
     */
    @Test
    public void testCreateSubProject_ShouldThrowCannotCreatePrivateProjectForPublicParent_WhenParentIsPublicAndProjectIsPrivate() {
        Project rootParent = Project.builder().id(3L).build();
        Project parentProject = Project.builder().id(1L).visibility(ProjectVisibility.PUBLIC).build();
        parentProject.setParentProject(rootParent);
        Project subproject1 = Project.builder().parentProject(parentProject).visibility(ProjectVisibility.PRIVATE).build();
        when(repository.getProjectById(1L)).thenReturn(subproject1);

        assertThrows(CannotCreatePrivateProjectForPublicParent.class, () -> {
            service.createSubProject(1L);
        });

        verify(repository, never()).save(any(Project.class));
    }

    /**
     * Получить все подпроекты проекта с фильтром по названию и статусу
     */
    @Test
    public void testGetAllSubProjectsWithFiltr_Case1_ProjectMatchesAllFilters() {
        Long childId1 = 1L;

        SubProjectDto parentProjectDto = SubProjectDto.builder()
                .children(List.of(childId1))
                .build();

        ProjectStatus statusFilter = ProjectStatus.IN_PROGRESS;
        String nameFilter = "Test Project";

        Project mockedChildResponse = Project.builder()
                .id(childId1)
                .name(nameFilter)
                .status(statusFilter)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        SubProjectDto expectedDto = SubProjectDto.builder().children(List.of())
                .status(statusFilter).visibility(ProjectVisibility.PUBLIC)
                .id(childId1)
                .name(nameFilter)
                .build();

        when(repository.findAllByIds(parentProjectDto.getChildren())).thenReturn(List.of(mockedChildResponse));

        List<SubProjectDto> result = service.getAllSubProjectsWithFiltr(parentProjectDto, nameFilter, statusFilter);

        assertEquals(1, result.size());
        assertEquals(List.of(expectedDto), result);
    }

    /**
     * Не забудьте проверить видимость проекта. Может быть такое, что публичный проект
     * имеет секретный подпроект, тогда его нельзя показывать другим участникам.
     */
    @Test
    public void testGetAllSubProjectsWithFiltr_Case2_ProjectIsPrivate() {
        Long childId1 = 1L;

        SubProjectDto parentProjectDto = SubProjectDto.builder()
                .children(List.of(childId1))
                .build();

        ProjectStatus statusFilter = ProjectStatus.IN_PROGRESS;
        String nameFilter = "Test Project";

        Project child1 = Project.builder()
                .id(childId1)
                .name("Test Project")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        List<Project> mockProjectList = List.of(child1);

        when(repository.findAllByIds(parentProjectDto.getChildren())).thenReturn(mockProjectList);

        List<SubProjectDto> result = service.getAllSubProjectsWithFiltr(parentProjectDto, nameFilter, statusFilter);

        assertEquals(0, result.size());
    }

    /**
     * если имена не совпадают то этот подпроект не должен вернутся
     */
    @Test
    public void testGetAllSubProjectsWithFiltr_Case3_NameDoesNotMatch() {
        Long childId1 = 1L;

        SubProjectDto parentProjectDto = SubProjectDto.builder()
                .children(List.of(childId1))
                .build();

        ProjectStatus statusFilter = ProjectStatus.IN_PROGRESS;
        String nameFilter = "Wrong Name";

        Project child1 = Project.builder()
                .id(childId1)
                .name("Test Project")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        List<Project> mockProjectList = List.of(child1);

        when(repository.findAllByIds(parentProjectDto.getChildren())).thenReturn(mockProjectList);

        List<SubProjectDto> result = service.getAllSubProjectsWithFiltr(parentProjectDto, nameFilter, statusFilter);

        assertEquals(0, result.size());
    }

    /**
     * если статус не совпадают то этот подпроект не должен вернутся
     */
    @Test
    public void testGetAllSubProjectsWithFiltr_Case4_StatusDoesNotMatch() {
        Long childId1 = 1L;

        SubProjectDto parentProjectDto = SubProjectDto.builder()
                .children(List.of(childId1))
                .build();

        ProjectStatus statusFilter = ProjectStatus.COMPLETED;
        String nameFilter = "Test Project";

        Project child1 = Project.builder()
                .id(childId1)
                .name("Test Project")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        List<Project> mockProjectList = List.of(child1);

        when(repository.findAllByIds(parentProjectDto.getChildren())).thenReturn(mockProjectList);

        List<SubProjectDto> result = service.getAllSubProjectsWithFiltr(parentProjectDto, nameFilter, statusFilter);

        assertEquals(0, result.size());
    }

}
