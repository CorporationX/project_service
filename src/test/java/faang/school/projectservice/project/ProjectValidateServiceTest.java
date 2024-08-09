package faang.school.projectservice.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.GeneralProjectInfoDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.project.ProjectValidateService;
import faang.school.projectservice.service.project.StageService;
import faang.school.projectservice.service.project.TeamService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidateServiceTest {
    @Mock
    private StageService stageService;;
    @Mock
    private TeamService teamService;
    @InjectMocks
    private ProjectValidateService projectValidateService;

    @Test
    public void testGetStages() {
        ProjectDto dto = new ProjectDto();
        dto.setStagesIds(List.of(1L, 2L, 3L));
        List<Long> foundStagesIds = projectValidateService.getStages(dto).stream().map(Stage::getStageId).toList();
        verify(stageService).findAllByIds(dto.getStagesIds());
    }

    @Test
    public void testGetNullStages() {
        ProjectDto dto = new ProjectDto();
        List<Stage> foundStages = projectValidateService.getStages(dto);
        assertNull(foundStages);
    }

    @Test
    public void testGetTeams() {
        ProjectDto dto = new ProjectDto();
        dto.setTeamsIds(List.of(1L, 2L, 3L));
        List<Long> foundTeamsIds = projectValidateService.getTeams(dto).stream().map(Team::getId).toList();
        verify(teamService).findTeamsByIds(dto.getTeamsIds());
    }

    @Test
    public void testGetNullTeams() {
        ProjectDto dto = new ProjectDto();
        List<Team> foundTeams = projectValidateService.getTeams(dto);
        assertNull(foundTeams);
    }

    @Test
    public void testGetName(){
        CreateSubProjectDto dto = new CreateSubProjectDto();
        dto.setName("Test");
        String resultName = projectValidateService.getName("Sun", dto);
        assertEquals(resultName, dto.getName());
    }

    @Test
    public void testGeNameDtoNameNullOrEmpty(){
        CreateSubProjectDto dto = new CreateSubProjectDto();
        String resultName = projectValidateService.getName("Test",dto);
        assertEquals("Test",resultName);
    }

    @Test
    public void testDtoParentProjectIdDoesNotMatchProjectIdAndNotNull(){
        CreateSubProjectDto dto = new CreateSubProjectDto();
        dto.setParentProjectId(2L);
        boolean result = projectValidateService.dtoParentProjectIdDoesNotMatchProjectIdAndNotNull(1L, dto);
        assertTrue(result);
    }

    @Test
    public void testVisibilityDtoAndProjectDoesNotMatches(){
        CreateSubProjectDto dto = new CreateSubProjectDto();
        dto.setVisibility(ProjectVisibility.PRIVATE);
        boolean result = projectValidateService.visibilityDtoAndProjectDoesNotMatches(ProjectVisibility.PUBLIC, dto);
        assertTrue(result);
    }

    @Test
    public void testStatusDtoAndProjectDoesNotMatches(){
        CreateSubProjectDto dto = new CreateSubProjectDto();
        dto.setStatus(ProjectStatus.COMPLETED);
        boolean result = projectValidateService.statusDtoAndProjectDoesNotMatches(ProjectStatus.CREATED, dto);
        assertTrue(result);
    }

}
