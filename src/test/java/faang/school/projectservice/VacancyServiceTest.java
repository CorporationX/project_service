package faang.school.projectservice;

import faang.school.projectservice.dto.vacancy.VacancyDto;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.VacancyValidator;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    private static final long PROJECT_ID = 1L;
    private static final long TEAM_ID = 1L;
    private static final String NAME_VACANCY = "TEST VACANCY NAME";
    private static final Long CREATOR_ID = 1L;
    private static final String PROJECT_NAME = "TEST PROJECT";
    private static final long VACANCY_ID_DTO = 1L;
    private static final long VACANCY_ID = 1L;
    private static final long CANDIDATE_ID = 1L;
    private static final int COUNT = 3;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @InjectMocks
    VacancyService vacancyService;

    VacancyDto vacancyDto;
    Vacancy vacancy;
    Vacancy vacancy2;
    Vacancy noIdVacancy;
    TeamMember creator;
    Candidate candidate;
    Team team;
    Project project;


    @BeforeEach
    void init() {
        project = Project.builder()
                .id(PROJECT_ID)
                .name(PROJECT_NAME)
                .build();
        team = Team.builder()
                .id(TEAM_ID)
                .project(project)
                .build();
        vacancyDto = VacancyDto.builder()
                .id(VACANCY_ID_DTO)
                .name(NAME_VACANCY)
                .projectId(PROJECT_ID)
                .createdBy(CREATOR_ID)
                .count(COUNT)
                .candidateIds(List.of(CANDIDATE_ID))
                .build();

        vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .name(NAME_VACANCY)
                .candidates(new ArrayList<>())
                .count(COUNT)
                .build();

        creator = TeamMember.builder()
                .id(CREATOR_ID)
                .team(team)
                .roles(List.of(TeamRole.OWNER))
                .build();

        noIdVacancy = Vacancy.builder()
                .name(NAME_VACANCY)
                .build();

        vacancy2 = Vacancy.builder()
                .id(5L)
                .build();

        candidate = new Candidate();
        candidate.setId(CANDIDATE_ID);
        candidate.setVacancy(vacancy);

    }

    @Test
    @DisplayName("Delete not existing vacancy")
    public void testDeleteVacancyNotExisting() {
        doThrow(DataValidationException.class).when(vacancyValidator).checkExistingOfVacancy(VACANCY_ID);
        assertThrows(DataValidationException.class, () -> vacancyService.deleteVacancyById(VACANCY_ID));
    }

    @Test
    @DisplayName("Successfully delete vacancy")
    public void testDeleteVacancySuccessfully() {
        candidate.setCandidateStatus(CandidateStatus.REJECTED);
        vacancy.setCandidates(List.of(candidate));

        when(vacancyRepository.getReferenceById(VACANCY_ID)).thenReturn(vacancy);
        doNothing().when(vacancyValidator).checkExistingOfVacancy(VACANCY_ID);
        doNothing().when(vacancyRepository).deleteById(VACANCY_ID);
        doNothing().when(teamMemberJpaRepository).deleteAllById(anyList());

        vacancyService.deleteVacancyById(VACANCY_ID);

        verify(vacancyValidator, times(1)).checkExistingOfVacancy(VACANCY_ID);
        verify(vacancyRepository, times(1)).getReferenceById(VACANCY_ID);
        verify(teamMemberJpaRepository, times(1)).deleteAllById(List.of(CANDIDATE_ID));
        verify(vacancyRepository, times(1)).deleteById(VACANCY_ID);
    }

    @Test
    @DisplayName("Test creating vacancy with non existing project")
    public void testCreateVacancyWithNonExistingProject() {
        String errorMessage = "The project doesn't exist in the System";
        doThrow(new DataValidationException(errorMessage)).when(vacancyValidator).validateExistingOfProject(vacancyDto.getProjectId());

        Exception exception = assertThrows(DataValidationException.class, () -> vacancyService.create(vacancyDto));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test creating vacancy : Creator is not member of the team")
    public void testCreateVacancyCreatorIsNotMemberOfTeam() {
        String errorMessage = "The creator doesn't work on the project";
        doThrow(new DataValidationException(errorMessage)).when(vacancyValidator).validateVacancyCreator(vacancyDto.getCreatedBy(), vacancyDto.getProjectId());

        Exception exception = assertThrows(DataValidationException.class, () -> vacancyService.create(vacancyDto));


        verifyNoInteractions(vacancyMapper, vacancyRepository);

        assertEquals(errorMessage, exception.getMessage());

    }

    @Test
    @DisplayName("Test creating vacancy : Successfully")
    public void testCreateVacancySuccessfully() {
        doNothing().when(vacancyValidator).validateExistingOfProject(vacancyDto.getProjectId());
        doNothing().when(vacancyValidator).validateVacancyCreator(vacancyDto.getCreatedBy(), vacancyDto.getProjectId());
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.create(vacancyDto);
        assertNotNull(result);
        assertEquals(vacancyDto.getId(), result.getId());
    }

    @Test
    @DisplayName("Test get vacancy by ID : Not existing ID")
    public void testGetVacancyByIdNotExistingId() {
        String errorMessage = "The vacancy doesn't exist in the system ID = " + VACANCY_ID;

        doThrow(new DataValidationException(errorMessage)).when(vacancyValidator).checkExistingOfVacancy(VACANCY_ID);
        Exception exception = assertThrows(DataValidationException.class, () -> vacancyService.getVacancyById(VACANCY_ID));

        verifyNoInteractions(vacancyRepository, vacancyMapper);
        assertEquals(exception.getMessage(), errorMessage);
    }

    @Test
    @DisplayName("Test get vacancy by ID : Successfully")
    public void testGetVacancyByIdSuccessfully() {
        doNothing().when(vacancyValidator).checkExistingOfVacancy(VACANCY_ID);
        when(vacancyRepository.getReferenceById(VACANCY_ID)).thenReturn(vacancy);

        VacancyDto returnedVacancyDto = vacancyMapper.toDto(vacancy);
        VacancyDto resultDto = vacancyService.getVacancyById(VACANCY_ID);
        assertNotNull(resultDto);
        assertEquals(returnedVacancyDto.getName(), resultDto.getName());
    }

}
