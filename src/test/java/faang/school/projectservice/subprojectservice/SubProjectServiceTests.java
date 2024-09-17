package faang.school.projectservice.subprojectservice;

import faang.school.projectservice.dto.subproject.ProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.filter.ProjectStatusFilter;
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
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
class SubProjectServiceTests {
    @Mock
    private ProjectRepository repository;
    @Spy
    private ProjectMapperImpl mapper = new ProjectMapperImpl();
    @Mock
    private SubProjectValidator validator;
    private List<ProjectFilter> projectFilters;

    private SubProjectServiceImpl service;

    @BeforeEach
    public void setUp() {
        projectFilters = List.of(new ProjectNameFilter(),new ProjectStatusFilter());
        service = new SubProjectServiceImpl(repository,mapper,validator, projectFilters);
    }
    /**
     * если валидация прошла то должна сохранится в базу
     */
    @Test
    public void if_validation_than_it_must_be_saved() {
        Project rootParent = Project.builder().id(3L).build();
        when(repository.getProjectById(1L)).thenReturn(rootParent);

        service.createSubProject(1L);

        verify(repository).save(rootParent);

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

    @Test
    @DisplayName("testing getSubProjects with selection correct subProject")
    public void testGetSubProjects() {
        long parentProjectId = 1L;
        long teamMemberId = 3L;

        var subProjectFirst = Project.builder()
                .name("ProjectName")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>()).build();

        Project subProjectSecond = Project.builder()
                .name("not")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>()).build();

        List<Project> subProjects = List.of(
                subProjectFirst,
                subProjectSecond
        );
        TeamMember teamMember = TeamMember.builder()
                .id(teamMemberId)
                .build();

        Team team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();

        var parentProject = Project.builder()
                .id(parentProjectId)
                .teams(List.of(team))
                .children(subProjects)
                .build();
       var subProjectFirstDto = SubProjectDto.builder()
                .name(subProjectFirst.getName())
                .build();
       var projectFilterDto = ProjectFilterDto.builder()
                .name("ProjectName")
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .build();

        when(repository.getProjectById(parentProject.getId())).thenReturn(parentProject);
        when(mapper.toDTO(subProjectFirst)).thenReturn(subProjectFirstDto);
        List<SubProjectDto> selectedSubProjects = service.getAllSubProjectsWithFiltr(parentProject.getId(), projectFilterDto);
        assertEquals(1, selectedSubProjects.size());
    }

}
