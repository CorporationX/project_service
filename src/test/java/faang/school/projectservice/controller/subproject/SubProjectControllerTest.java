package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.subproject.SubProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {
    @Mock
    SubProjectMapper subProjectMapper;
    @Mock
    private SubProjectService subProjectService;
    @InjectMocks
    private SubProjectController subProjectController;

    @Test
    void createSubProject() {
        SubProjectDto subProjectDto = new SubProjectDto();
        subProjectDto.setName("test");
        subProjectDto.setDescription("test");
        subProjectDto.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        subProjectDto.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        subProjectDto.setId(1L);
        subProjectDto.setParentProjectId(2L);
        subProjectDto.setChildIds(List.of(1L, 2L));
        subProjectDto.setStatus(ProjectStatus.CREATED);
        subProjectDto.setMomentIds(List.of(1L, 2L));
        subProjectDto.setVacancyIds(List.of(1L, 2L));
        subProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        Project project = new Project();
        project.setName("test");
        project.setDescription("test");
        project.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        project.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        project.setId(1L);
        project.setParentProject(Project.builder().build());
        project.setChildren(List.of(Project.builder().build()));
        project.setStatus(ProjectStatus.CREATED);
        project.setMoments(List.of(new Moment()));
        project.setVacancies(List.of(Vacancy.builder().build()));
        project.setVisibility(ProjectVisibility.PUBLIC);

        Mockito.when(subProjectMapper.toEntity(subProjectDto)).thenReturn(project);
        Mockito.doNothing().when(subProjectService).createSubProject(1L, project);

        Assertions.assertEquals(new ResponseEntity<>(HttpStatus.CREATED),
                subProjectController.createSubProject(1L, subProjectDto));
    }

    @Test
    void updateSubProject() {
        SubProjectDto subProjectDto = new SubProjectDto();
        subProjectDto.setName("test");
        subProjectDto.setDescription("test");
        subProjectDto.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        subProjectDto.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        subProjectDto.setId(1L);
        subProjectDto.setParentProjectId(2L);
        subProjectDto.setChildIds(List.of(1L, 2L));
        subProjectDto.setStatus(ProjectStatus.CREATED);
        subProjectDto.setMomentIds(List.of(1L, 2L));
        subProjectDto.setVacancyIds(List.of(1L, 2L));
        subProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        Project project = new Project();
        project.setName("test");
        project.setDescription("test");
        project.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        project.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0));
        project.setId(1L);
        project.setParentProject(Project.builder().build());
        project.setChildren(List.of(Project.builder().build()));
        project.setStatus(ProjectStatus.CREATED);
        project.setMoments(List.of(new Moment()));
        project.setVacancies(List.of(Vacancy.builder().build()));
        project.setVisibility(ProjectVisibility.PUBLIC);

        Mockito.when(subProjectMapper.toEntity(subProjectDto)).thenReturn(project);
        Mockito.doNothing().when(subProjectService).updateSubProject(1L, project);

        Assertions.assertEquals(new ResponseEntity<>(HttpStatus.OK),
                subProjectController.updateSubProject(1L, subProjectDto));
    }

    @Test
    void getSubProjectsByFilters() {
        List<Project> projects = List.of(Project.builder()
                        .id(1L)
                        .build(),
                Project.builder()
                        .id(2L)
                        .build());

        List<SubProjectDto> subProjectDtos = new ArrayList<>();
        SubProjectDto subProjectDto = new SubProjectDto();
        subProjectDto.setId(1L);
        subProjectDtos.add(subProjectDto);
        SubProjectDto subProjectDto2 = new SubProjectDto();
        subProjectDto2.setId(2L);
        subProjectDtos.add(subProjectDto2);
        SubProjectFilterDto filterDto = SubProjectFilterDto.builder().build();

        Mockito.when(subProjectService.getSubProjectsByProjectId(2L, filterDto)).thenReturn(projects);
        Mockito.when(subProjectMapper.toDto(projects)).thenReturn(List.of(subProjectDto, subProjectDto2));

        ResponseEntity<List<SubProjectDto>> expected = new ResponseEntity<>(subProjectDtos, HttpStatus.OK);
        ResponseEntity<List<SubProjectDto>> actual = subProjectController.getSubProjectsByFilters(2L, filterDto);

        Assertions.assertEquals(expected.getBody(), actual.getBody());
        Mockito.verify(subProjectService, Mockito.times(1)).getSubProjectsByProjectId(2L, filterDto);
        Mockito.verify(subProjectMapper, Mockito.times(1)).toDto(projects);
    }
}