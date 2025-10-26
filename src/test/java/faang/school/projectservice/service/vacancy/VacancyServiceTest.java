package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.CandidateCreateDto;
import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private VacancyValidator vacancyValidator;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private VacancyService vacancyService;

    private VacancyCreateDto vacancyCreateDto;
    private Vacancy vacancy;
    private VacancyDto vacancyDto;
    private Project project;
    private VacancyUpdateDto vacancyUpdateDto;
    private TeamMember teamMember;
    private Candidate candidate;
    private CandidateCreateDto candidateCreateDto;
    private CandidateDto candidateDto;

    @BeforeEach
    void setUp() {
        vacancyCreateDto = new VacancyCreateDto(
                1L,
                "Java Developer needed",
                TeamRole.DEVELOPER,
                3,
                "123"
        );

        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .teams(new ArrayList<>())
                .build();

        Team team = Team.builder()
                .id(1L)
                .project(project)
                .teamMembers(new ArrayList<>())
                .build();
        project.getTeams().add(team);

        vacancy = Vacancy.builder()
                .id(1L)
                .name("Java Developer")
                .description("Java Developer needed")
                .position(TeamRole.DEVELOPER)
                .project(project)
                .status(VacancyStatus.OPEN)
                .count(3)
                .candidates(new ArrayList<>())
                .acceptedCandidates(new ArrayList<>())
                .build();

        vacancyDto = new VacancyDto(
                1L,
                "Java Developer needed",
                TeamRole.DEVELOPER,
                3,
                VacancyStatus.OPEN,
                null
        );

        vacancyUpdateDto = new VacancyUpdateDto(
                "Updated description",
                TeamRole.MANAGER,
                5
        );

        teamMember = TeamMember.builder()
                .id(1L)
                .userId(123L)
                .nickname("Test User")
                .roles(List.of(TeamRole.MANAGER))
                .build();

        candidate = Candidate.builder()
                .id(1L)
                .userId(456L)
                .username("Candidate User")
                .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                .isAccepted(false)
                .vacancy(vacancy)
                .build();

        candidateCreateDto = new CandidateCreateDto(
                456L, // userId кандидата
                "Candidate User",
                "Java Developer"
        );

        candidateDto = new CandidateDto(
                1L, // candidateId
                "Candidate User",
                "Java Developer"
        );

        // Add team member to project team
        project.getTeams().get(0).getTeamMembers().add(teamMember);
    }

    @Test
    void createVacancySuccess() {

        when(userContext.getUserId()).thenReturn(123L);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);
        when(projectRepository.getByIdOrThrow(1L)).thenReturn(project);
        when(vacancyMapper.toVacancy(vacancyCreateDto)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.createVacancy(vacancyCreateDto);

        assertNotNull(result);
        assertEquals(vacancyDto, result);
        verify(vacancyValidator).validateRole(teamMember);
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    void updateVacancySuccess() {

        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.updateVacancy(1L, vacancyUpdateDto);

        assertNotNull(result);
        assertEquals(vacancyDto, result);
        verify(vacancyValidator).validateRole(teamMember);
        verify(vacancyMapper).updateVacancyFromDto(vacancyUpdateDto, vacancy);
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    void addCandidatesToVacancySuccess() {

        vacancy.setCandidates(new ArrayList<>());
        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);
        when(vacancyMapper.toCandidate(candidateCreateDto)).thenReturn(candidate);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.addCandidatesToVacancy(1L, candidateCreateDto);

        assertNotNull(result);
        assertEquals(vacancyDto, result);
        assertTrue(vacancy.getCandidates().contains(candidate));
        verify(vacancyValidator).validateRole(teamMember);
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    void addCandidatesToVacancyWhenVacancyClosedThrowsException() {

        vacancy.setStatus(VacancyStatus.CLOSED);
        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);

        assertThrows(IllegalStateException.class,
                () -> vacancyService.addCandidatesToVacancy(1L, candidateCreateDto));
    }

    @Test
    void addCandidatesToVacancyWhenCandidateIsProjectMemberThrowsException() {

        TeamMember existingMember = TeamMember.builder()
                .userId(456L) // Same as candidate userId
                .build();
        project.getTeams().get(0).getTeamMembers().add(existingMember);

        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);

        assertThrows(IllegalArgumentException.class,
                () -> vacancyService.addCandidatesToVacancy(1L, candidateCreateDto));
    }

    @Test
    void addCandidatesToVacancyWhenCandidateAlreadyAddedThrowsException() {

        vacancy.getCandidates().add(candidate); // candidate имеет userId = 456L
        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);

        assertThrows(IllegalArgumentException.class,
                () -> vacancyService.addCandidatesToVacancy(1L, candidateCreateDto));
    }

    @Test
    void acceptCandidateSuccess() {

        vacancy.getCandidates().add(candidate);
        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);
        when(vacancyMapper.toCandidateDto(candidate)).thenReturn(candidateDto);

        CandidateDto result = vacancyService.acceptCandidate(1L, 1L);

        assertNotNull(result);
        assertEquals(candidateDto, result);
        assertEquals(CandidateStatus.ACCEPTED, candidate.getCandidateStatus());
        assertTrue(candidate.getIsAccepted());
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    void acceptCandidateWhenCandidateNotFoundThrowsException() {

        vacancy.getCandidates().add(candidate); // Добавляем кандидата с id = 1L
        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);

        //  ищем кандидата с несуществующим id
        assertThrows(IllegalArgumentException.class,
                () -> vacancyService.acceptCandidate(1L, 999L));
    }

    @Test
    void acceptCandidateWhenCandidateAlreadyAcceptedThrowsException() {

        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);
        candidate.setIsAccepted(true);
        vacancy.getAcceptedCandidates().add(candidate);
        vacancy.getCandidates().add(candidate);

        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);

        assertThrows(IllegalStateException.class,
                () -> vacancyService.acceptCandidate(1L, 1L));
    }

    @Test
    void rejectCandidateSuccess() {

        vacancy.getCandidates().add(candidate);
        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);
        when(vacancyMapper.toCandidateDto(candidate)).thenReturn(candidateDto);

        CandidateDto result = vacancyService.rejectCandidate(1L, 1L);

        assertNotNull(result);
        assertEquals(candidateDto, result);
        assertEquals(CandidateStatus.REJECTED, candidate.getCandidateStatus());
        assertFalse(candidate.getIsAccepted());
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    void waitingResponseSuccess() {

        vacancy.getCandidates().add(candidate);
        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);
        when(vacancyMapper.toCandidateDto(candidate)).thenReturn(candidateDto);

        CandidateDto result = vacancyService.waitingResponse(1L, 1L);

        assertNotNull(result);
        assertEquals(candidateDto, result);
        assertEquals(CandidateStatus.WAITING_RESPONSE, candidate.getCandidateStatus());
        assertFalse(candidate.getIsAccepted());
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    void closeVacancySuccess() {

        Candidate acceptedCandidate = Candidate.builder()
                .id(2L)
                .userId(789L)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .isAccepted(true)
                .build();
        vacancy.getAcceptedCandidates().add(acceptedCandidate);
        vacancy.setCount(1); // Only need 1 candidate

        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.closeVacancy(1L);

        assertNotNull(result);
        assertEquals(VacancyStatus.CLOSED, vacancy.getStatus());
        assertEquals(vacancyDto, result);
    }

    @Test
    void closeVacancyWhenNotEnoughCandidatesThrowsException() {

        vacancy.setCount(3);
        vacancy.getAcceptedCandidates().clear(); // No accepted candidates

        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);

        assertThrows(IllegalStateException.class,
                () -> vacancyService.closeVacancy(1L));
    }

    @Test
    void getVacancySuccess() {

        when(vacancyRepository.getWithCandidatesOrThrow(1L)).thenReturn(vacancy);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.getVacancy(1L);

        assertNotNull(result);
        assertEquals(vacancyDto, result);
    }

    @Test
    void getAllVacanciesSuccess() {

        List<Vacancy> vacancies = Arrays.asList(vacancy);
        List<VacancyDto> vacancyDtos = Arrays.asList(vacancyDto);

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        List<VacancyDto> result = vacancyService.getAllVacancies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(vacancyDtos, result);
    }

    @Test
    void findVacancyByDescriptionAndPositionSuccess() {

        String description = "Java";
        TeamRole position = TeamRole.DEVELOPER;
        List<Vacancy> vacancies = Arrays.asList(vacancy);
        List<VacancyDto> vacancyDtos = Arrays.asList(vacancyDto);

        when(vacancyRepository.findVacancyByFilters(description, position)).thenReturn(vacancies);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        List<VacancyDto> result = vacancyService.findVacancyByDescriptionAndPosition(description, position);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(vacancyDtos, result);
    }

    @Test
    void deleteVacancySuccess() {

        when(userContext.getUserId()).thenReturn(123L);
        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(123L, 1L)).thenReturn(teamMember);

        vacancyService.deleteVacancy(1L);

        verify(vacancyValidator).validateRole(teamMember);
        verify(vacancyRepository).deleteById(1L);
    }
}
