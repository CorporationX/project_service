package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.subproject.filter.SubProjectFilter;
import faang.school.projectservice.service.subproject.impl.SubProjectServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private SubProjectMapper subProjectMapper = Mappers.getMapper(SubProjectMapper.class);

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private SubProjectFilter subProjectFilter;

    @InjectMocks
    private SubProjectServiceImpl subProjectService;

    private Moment moment;
    private Project subProject;
    private Project parentProject;
    private SubProjectDto subProjectDto;
    private SubProjectFilterDto subProjectFilterDto;

    @BeforeEach
    void setUp() {
        subProjectDto = SubProjectDto.builder()
                .id(2L)
                .name("Test")
                .description("SomeTest")
                .ownerId(1L)
                .parentProjectId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        moment = new Moment();
        moment.setUserIds(List.of(1L));

        subProject = subProjectMapper.toProject(subProjectDto);

        parentProject = subProjectMapper.toProject(SubProjectDto.builder()
                .id(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build());

        subProjectFilterDto = SubProjectFilterDto.builder()
                .namePattern("test")
                .status(ProjectStatus.CREATED)
                .build();
        List<SubProjectFilter> filters = List.of(subProjectFilter);
        subProjectService = new SubProjectServiceImpl(projectRepository, subProjectMapper, momentRepository, filters);
    }

    @Test
    @DisplayName("Creating SubProject with not exists parentId")
    public void testCreateSubProjectWithNotFoundParentProject() {
        when(projectRepository.getProjectById(1L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> subProjectService.create(subProjectDto));
    }

    @Test
    @DisplayName("Creating SubProject with different visibility")
    public void testCreateSubProjectWithDifferentVisibility() {
        subProjectDto.setVisibility(ProjectVisibility.PRIVATE);

        when(projectRepository.getProjectById(1L)).thenReturn(parentProject);

        assertThrows(IllegalArgumentException.class, () -> subProjectService.create(subProjectDto));
    }

    @Test
    @DisplayName("Creating SubProject success")
    public void testCreateSubProjectSuccess() {
        subProjectDto.setStatus(null);
        subProjectDto.setVisibility(null);
        when(projectRepository.getProjectById(1L)).thenReturn(parentProject);
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);

        subProjectService.create(subProjectDto);

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Updating subProject where project not exists")
    public void testUpdateProjectWithProjectNotFound() {
        when(projectRepository.getProjectById(2L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> subProjectService.update(subProjectDto));
    }

    @Test
    @DisplayName("Updating subProject with different statuses")
    public void testUpdateSubProjectWithDifferentChildrenStatuses() {
        Project childProject = subProjectMapper.toProject(SubProjectDto.builder()
                .id(3L)
                .status(ProjectStatus.IN_PROGRESS)
                .build());
        subProject.setChildren(List.of(childProject));

        when(projectRepository.getProjectById(2L)).thenReturn(subProject);

        assertThrows(IllegalArgumentException.class, () -> subProjectService.update(subProjectDto));
    }

    @Test
    @DisplayName("Updating SubProject success")
    public void testUpdateSubProjectSuccess() {
        subProject.setChildren(new ArrayList<>());
        when(projectRepository.getProjectById(2L)).thenReturn(subProject);
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);

        subProjectService.update(subProjectDto);

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Updating SubProject where all subprojects are completed")
    public void testUpdateSubProjectWhereSubProjectsAreCompleted() {
        subProjectDto.setStatus(ProjectStatus.COMPLETED);
        Team team = new Team();
        team.setTeamMembers(new ArrayList<>());
        subProject.setTeams(List.of(team));
        subProject.setMoments(new ArrayList<>());
        Project childProject = subProjectMapper.toProject(SubProjectDto.builder()
                .id(3L)
                .status(ProjectStatus.COMPLETED)
                .build());
        subProject.setChildren(List.of(childProject));

        when(projectRepository.getProjectById(2L)).thenReturn(subProject);
        when(momentRepository.save(any(Moment.class))).thenReturn(moment);
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);

        subProjectService.update(subProjectDto);

        verify(momentRepository).save(any(Moment.class));
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Updating SubProject where parent project's visibility is private")
    public void testUpdateSubProjectWhereSubProjectVisibilityIsPrivate() {
        subProjectDto.setVisibility(ProjectVisibility.PRIVATE);
        Project childProject = subProjectMapper.toProject(SubProjectDto.builder()
                .id(3L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build());
        subProject.setChildren(List.of(childProject));

        when(projectRepository.getProjectById(2L)).thenReturn(subProject);
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);
        subProjectService.update(subProjectDto);

        assertEquals(ProjectVisibility.PRIVATE, subProject.getChildren().get(0).getVisibility());
    }

    @Test
    @DisplayName("Getting subProjects by project id")
    public void testGetSubProjectsByProjectId() {
        Project testProject = subProjectMapper.toProject(SubProjectDto.builder()
                .id(4L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .parentProjectId(1L)
                .build());
        List<Project> subProjects = List.of(subProject, testProject);
        Stream<Project> subProjectStream = subProjects.stream();
        parentProject.setChildren(subProjects);

        when(projectRepository.getProjectById(1L)).thenReturn(parentProject);
        when(subProjectFilter.isApplicable(subProjectFilterDto)).thenReturn(true);
        when(subProjectFilter.apply(any(), any(SubProjectFilterDto.class))).thenReturn(subProjectStream);

        List<SubProjectDto> result = subProjectService.findSubProjectsByParentId(1L, subProjectFilterDto);

        assertEquals(2, result.size());
        verify(projectRepository).getProjectById(1L);
        verify(subProjectFilter).isApplicable(subProjectFilterDto);
        verify(subProjectFilter).apply(any(), any(SubProjectFilterDto.class));
    }
}
