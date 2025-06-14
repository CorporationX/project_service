package project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.specification.ProjectSpecification;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.Assertions;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private ProjectSpecification projectSpecification;
    @Mock
    private TeamRepository teamRepository;
    @Spy
    private ProjectMapperImpl projectMapper;
    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    private final static long CREATOR_ID = 1L;
    private final static String NAME_PROJECT = "Test";
    private final static String DESCRIPTION = "description";
    private final static long PROJECT_ID = 1L;
    private ProjectDto projectDtoTest = preparationProjectDto(NAME_PROJECT, DESCRIPTION);

    @Test
    public void shouldCreateProjectWhenValidInput() {

        projectService.create(projectDtoTest, CREATOR_ID);
        verify(projectRepository, times(1)).save(projectCaptor.capture());
        Project result = projectCaptor.getValue();

        Assertions.assertEquals(DESCRIPTION, result.getDescription(), "description не совпало с ожидаемым");
        Assertions.assertEquals(NAME_PROJECT, result.getName(), "имя проекта не совпало");
        Assertions.assertEquals(CREATOR_ID, result.getOwnerId(), "Имя создателя не совпало");
        Assertions.assertEquals(ProjectVisibility.PUBLIC, result.getVisibility());
    }

    @Test
    public void shouldUpdateWhenProjectProjectDoesNotExist() {
        when(projectRepository.findById(CREATOR_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class,
                () -> projectService.update(CREATOR_ID, projectProjectUpdateDto()));
    }

    @Test
    public void shouldUpdateWhenValidInput() {
        when(projectRepository.findById(PROJECT_ID))
                .thenReturn(Optional.of(preparationProject()));
        when(teamRepository.findAllById(List.of(2L, 3L)))
                .thenReturn(List.of(preparationTeam(2L), preparationTeam(2L)));

        ProjectDto result = projectService.update(CREATOR_ID, projectProjectUpdateDto());

        Assertions.assertEquals(ProjectVisibility.PRIVATE, result.getVisibility());
        Assertions.assertEquals(ProjectStatus.IN_PROGRESS, result.getStatus());
        Assertions.assertEquals(2, result.getTeams().size());
        Assertions.assertEquals(NAME_PROJECT, result.getName());
        Assertions.assertEquals(DESCRIPTION, result.getDescription());
    }

    @Test
    public void shouldFindByIdProjectWhenProjectNotFound() {
        when(projectRepository.findById(CREATOR_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataValidationException.class,
                () -> projectService.findByIdProject(CREATOR_ID));
    }

    @Test void shouldFindByIdProjectWhenValidInput(){
        when(projectRepository.findById(CREATOR_ID)).thenReturn(Optional.of(preparationProject()));

        ProjectDto result = projectService.findByIdProject(CREATOR_ID);

        Assertions.assertEquals(ProjectVisibility.PUBLIC, result.getVisibility());
        Assertions.assertEquals(ProjectStatus.CREATED, result.getStatus());
        Assertions.assertEquals(NAME_PROJECT, result.getName());
        Assertions.assertEquals(DESCRIPTION, result.getDescription());
    }

    private Team preparationTeam(long teamId) {
        return Team.builder()
                .id(teamId)
                .build();
    }

    private Project preparationProject() {
        return Project.builder()
                .id(PROJECT_ID)
                .name(NAME_PROJECT)
                .description(DESCRIPTION)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private ProjectDto preparationProjectDto(String projectName, String description) {
        return ProjectDto.builder()
                .name(projectName)
                .description(description)
                .build();
    }

    private ProjectDto projectProjectUpdateDto() {
        return ProjectDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .teams(List.of(2L, 3L))
                .visibility(ProjectVisibility.PRIVATE)
                .build();
    }
}