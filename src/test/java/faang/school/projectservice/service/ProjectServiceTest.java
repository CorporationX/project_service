package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.project.ResponseProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ResponseProjectMapper responseProjectMapper = ResponseProjectMapper.INSTANCE;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void getAllByFilterTest() {
        List<ProjectFilter> filterList = List.of(new ProjectNameFilter(), new ProjectStatusFilter());
        projectService = new ProjectService(projectRepository, responseProjectMapper, filterList);
        ProjectFilterDto filterDto = new ProjectFilterDto(1L, "Name", ProjectStatus.CREATED);
        Project wrongName = Project.builder().ownerId(1L).visibility(ProjectVisibility.PUBLIC).name("Wrong").build();
        Project wrongStatus = Project.builder().ownerId(1L).visibility(ProjectVisibility.PUBLIC).name("Name").status(ProjectStatus.IN_PROGRESS).build();
        Project allConditions = Project.builder().ownerId(1L).visibility(ProjectVisibility.PUBLIC).name("Name").status(ProjectStatus.CREATED).build();
        List<Project> projects = new ArrayList<>(List.of(wrongName, wrongStatus, allConditions));

        when(projectRepository.findAllByVisibilityOrOwnerId(ProjectVisibility.PUBLIC, 1L)).thenReturn(projects);

        List<ResponseProjectDto> result = projectService.getAllByFilter(filterDto, 1);

        assertEquals(1, result.size());
    }
}