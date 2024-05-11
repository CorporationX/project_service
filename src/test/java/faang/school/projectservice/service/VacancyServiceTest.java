package faang.school.projectservice.service;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    private static final long SAVED_VACANCY_ID = 1L;
    private static final String VACANCY_NAME = "Java Developer";
    private static final long CURATOR_ID = 1L;
    private static final long PROJECT_ID = 1L;
    private static final long CANDIDATE_ID = 1L;
    private static final long NOT_EXIST_CANDIDATE_ID = 99L;
    private static final Long TEAM_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Integer COUNT_CANDIDATE = 1;
    private static final Double SALARY = 200000.0;
    private static final Long TEAM_MEMBER_ID = 3L;

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private VacancyMapper vacancyMapper;

    @InjectMocks
    private VacancyService vacancyService;

    VacancyDto vacancyDto;
    Vacancy vacancy;
    Vacancy vacancy2;
    Vacancy noIdVacancy;
    TeamMember curator;
    Candidate candidate;
    Team team;
    Project project;

    @BeforeEach
    public void init() {
        project = Project.builder()
                .id(PROJECT_ID)
                .build();
        team = Team.builder()
                .id(TEAM_ID)
                .project(project)
                .build();
        vacancyDto = VacancyDto.builder()
                .name(VACANCY_NAME)
                .status(VacancyStatus.OPEN)
                .projectId(PROJECT_ID)
                .createdBy(CURATOR_ID)
                .count(COUNT_CANDIDATE)
                .candidateIds(List.of(CANDIDATE_ID))
                .build();
        vacancy = Vacancy.builder()
                .id(SAVED_VACANCY_ID)
                .name(VACANCY_NAME)
                .candidates(new ArrayList<>())
                .count(COUNT_CANDIDATE)
                .build();
        vacancy2 = Vacancy.builder()
                .id(999L)
                .build();
        noIdVacancy = Vacancy.builder()
                .name(VACANCY_NAME)
                .build();
        curator = TeamMember.builder()
                .id(CURATOR_ID)
                .team(team)
                .roles(List.of(TeamRole.MANAGER))
                .build();

        candidate = new Candidate();
        candidate.setId(CANDIDATE_ID);
        candidate.setUserId(USER_ID);
        candidate.setVacancy(vacancy);

    }

    @Test
    @DisplayName("Create a vacancy when status not open")
    public void createWhenStatusNotOpen() {
        vacancyDto.setStatus(VacancyStatus.CLOSED);
        String errMessage = "New vacancy must be with status OPEN";

        doThrow(new DataValidationException("New vacancy must be with status OPEN"))
                .when(vacancyValidator).validateCreateVacancyStatus(vacancyDto);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> vacancyService.create(vacancyDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Create a vacancy when curator not exists")
    public void createWhenCuratorNotExists() {
        String errMessage = "Team member doesn't exist by id: " + CURATOR_ID;

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> vacancyService.create(vacancyDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Create a vacancy when curator doesn't have corresponding role OWNER or MANAGER")
    public void createWhenCuratorHasNotCorrespondingRole() {
        curator.setRoles(List.of(TeamRole.DEVELOPER));
        String errMessage = "The supervisor does not have the appropriate role for hiring employees";

        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));
        doThrow(new DataValidationException(errMessage))
                .when(vacancyValidator).validateCuratorRole(curator);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> vacancyService.create(vacancyDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Create a vacancy when project not exist")
    public void createWhenProjectNotExists() {
        String errMessage = "Project doesn't exist by id: " + PROJECT_ID;
        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));
        doThrow(new EntityNotFoundException(errMessage))
                .when(vacancyValidator).validateProject(vacancyDto, curator);

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> vacancyService.create(vacancyDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Create a vacancy when curator not in vacancy's project")
    public void createWhenCuratorNotInProject() {
        String errMessage = "The curator is not from the project team";

        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));
        doThrow(new DataValidationException(errMessage))
                .when(vacancyValidator).validateProject(vacancyDto, curator);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> vacancyService.create(vacancyDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Create a vacancy when candidate doesn't exist")
    public void createWhenCandidateNotExists() {
        String errMessage = "Candidate doesn't exist by id: " + NOT_EXIST_CANDIDATE_ID;
        vacancyDto.setCandidateIds(List.of(NOT_EXIST_CANDIDATE_ID));

        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> vacancyService.create(vacancyDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Create a vacancy when candidate in another vacancy")
    public void createWhenCandidateInAnotherVacancy() {
        String errMessage = String.format("Candidate with ID: %s is already in another vacancy", CANDIDATE_ID);
        candidate.setVacancy(vacancy2);

        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.ofNullable(candidate));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(noIdVacancy);
        when(vacancyRepository.save(noIdVacancy)).thenReturn(vacancy);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> vacancyService.create(vacancyDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Create a vacancy when everything is fine")
    public void createWhenOk() {
        vacancyDto.setId(SAVED_VACANCY_ID);
        candidate.setVacancy(null);

        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.ofNullable(candidate));
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(noIdVacancy);
        when(vacancyRepository.save(noIdVacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto actualResult = vacancyService.create(vacancyDto);
        assertEquals(vacancyDto, actualResult);
    }

    @Test
    @DisplayName("Update a vacancy when it is not found")
    public void updateWhenVacancyNotExists() {
        String errMessage = String.format("Vacancy doesn't exist by id: %s", SAVED_VACANCY_ID);

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> vacancyService.update(vacancyDto, SAVED_VACANCY_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Update a vacancy when curator not exists")
    public void updateWhenCuratorNotExists() {
        String errMessage = "Team member doesn't exist by id: " + CURATOR_ID;

        when(vacancyRepository.findById(SAVED_VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> vacancyService.update(vacancyDto, SAVED_VACANCY_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Update a vacancy when project doesn't exist")
    public void updateWhenProjectNotExists() {
        String errMessage = "Project doesn't exist by id: " + PROJECT_ID;

        when(vacancyRepository.findById(SAVED_VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));
        doThrow(new EntityNotFoundException(errMessage))
                .when(vacancyValidator).validateProject(vacancyDto, curator);

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> vacancyService.update(vacancyDto, SAVED_VACANCY_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Update a vacancy when candidate doesn't exist")
    public void updateWhenCandidateNotExists() {
        String errMessage = "Candidate doesn't exist by id: " + NOT_EXIST_CANDIDATE_ID;
        vacancyDto.setCandidateIds(List.of(NOT_EXIST_CANDIDATE_ID));

        when(vacancyRepository.findById(SAVED_VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> vacancyService.update(vacancyDto, SAVED_VACANCY_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Update a vacancy when candidate in another vacancy")
    public void updateWhenCandidateInAnotherVacancy() {
        String errMessage = String.format("Candidate with ID: %s is already in another vacancy", CANDIDATE_ID);
        vacancy.setCandidates(new ArrayList<>());
        candidate.setVacancy(vacancy2);

        when(vacancyRepository.findById(SAVED_VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.ofNullable(candidate));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> vacancyService.update(vacancyDto, SAVED_VACANCY_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Update a vacancy when candidates not enough to close vacancy")
    public void updateWhenNotEnoughCandidates() {
        String errMessage = "Vacancy can't be CLOSE, if not enough candidates";
        vacancy.setCount(5);
        vacancyDto.setStatus(VacancyStatus.CLOSED);
        vacancy.getCandidates().add(candidate);

        when(vacancyRepository.findById(SAVED_VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.ofNullable(candidate));
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        doThrow(new DataValidationException(errMessage)).when(vacancyValidator).validateUpdateVacancyStatus(vacancyDto.getStatus(), vacancy);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> vacancyService.update(vacancyDto, SAVED_VACANCY_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Update a vacancy when everything is fine")
    public void updateWhenOk() {
        vacancyDto.setStatus(VacancyStatus.CLOSED);
        vacancy.getCandidates().add(candidate);
        vacancy.setProject(project);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCreatedBy(CURATOR_ID);

        Vacancy updatedVacancy = Vacancy.builder()
                .id(SAVED_VACANCY_ID)
                .name(VACANCY_NAME)
                .status(VacancyStatus.CLOSED)
                .project(project)
                .createdBy(CURATOR_ID)
                .salary(SALARY)
                .candidates(List.of(candidate))
                .count(COUNT_CANDIDATE)
                .build();
        VacancyDto updatedVacancyDto = VacancyDto.builder()
                .id(SAVED_VACANCY_ID)
                .name(VACANCY_NAME)
                .status(VacancyStatus.CLOSED)
                .projectId(PROJECT_ID)
                .createdBy(CURATOR_ID)
                .salary(SALARY)
                .count(COUNT_CANDIDATE)
                .candidateIds(List.of(CANDIDATE_ID))
                .build();

        when(vacancyRepository.findById(SAVED_VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
        when(teamMemberJpaRepository.findById(CURATOR_ID)).thenReturn(Optional.ofNullable(curator));
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.ofNullable(candidate));
        when(vacancyRepository.save(vacancy)).thenReturn(updatedVacancy);
        when(vacancyMapper.toDto(updatedVacancy)).thenReturn(updatedVacancyDto);

        VacancyDto actualResult = vacancyService.update(vacancyDto, SAVED_VACANCY_ID);

        assertEquals(updatedVacancyDto, actualResult);
    }

    @Test
    @DisplayName("Delete vacancy when vacancy not found")
    public void deleteWhenVacancyNotFound() {
        String errMessage = "Vacancy with id " + SAVED_VACANCY_ID + " not found!";

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> vacancyService.delete(SAVED_VACANCY_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Delete vacancy when when everything is fine")
    public void deleteWhenOk() {
        TeamMember teamMember = TeamMember.builder()
                .id(TEAM_MEMBER_ID)
                .userId(USER_ID)
                .roles(Collections.emptyList())
                .build();
        vacancy.getCandidates().add(candidate);
        vacancy.setProject(project);

        when(vacancyRepository.findById(SAVED_VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
        when(teamMemberJpaRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(teamMember);

        vacancyService.delete(SAVED_VACANCY_ID);

        verify(teamMemberJpaRepository).deleteById(TEAM_MEMBER_ID);
        verify(vacancyRepository).deleteById(SAVED_VACANCY_ID);
    }

    @Test
    @DisplayName("Find vacancy by ID when it not found")
    public void findByIdWhenNotFound() {
        String errMessage = "Vacancy with id " + SAVED_VACANCY_ID + " not found!";

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> vacancyService.findById(SAVED_VACANCY_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    @DisplayName("Find vacancy")
    public void findByIdWhenFound() {
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCreatedBy(CURATOR_ID);
        vacancy.setProject(project);
        vacancy.setCandidates(List.of(candidate));

        when(vacancyRepository.findById(SAVED_VACANCY_ID)).thenReturn(Optional.ofNullable(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto actualResult = vacancyService.findById(SAVED_VACANCY_ID);
        assertEquals(vacancyDto, actualResult);
    }
}