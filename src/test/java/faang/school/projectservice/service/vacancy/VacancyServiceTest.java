package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.common.PageResponse;
import faang.school.projectservice.dto.vacancy.CandidateCreateDto;
import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.dto.vacancy.SearchDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.vacancy.CandidateMapper;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
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
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Spy
    private CandidateMapper candidateMapper = Mappers.getMapper(CandidateMapper.class);

    @Spy
    private VacancyValidator vacancyValidator;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private VacancyService vacancyService;

    @Captor
    private ArgumentCaptor<Vacancy> vacancyCaptor;

    private Vacancy vacancy;
    private Project project;
    private TeamMember author;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .teams(new ArrayList<>())
                .build();

        author = TeamMember.builder()
                .id(1L)
                .userId(100L)
                .roles(List.of(TeamRole.OWNER))
                .build();

        List<Candidate> candidates = new ArrayList<>();
        List<Candidate> acceptedCandidates = new ArrayList<>();

        vacancy = Vacancy.builder()
                .id(1L)
                .name("Java Developer")
                .description("Looking for experienced Java developer")
                .position(TeamRole.DEVELOPER)
                .project(project)
                .count(3)
                .candidates(candidates)
                .acceptedCandidates(acceptedCandidates)
                .build();

        candidate = Candidate.builder()
                .id(1L)
                .userId(200L)
                .username("candidate1")
                .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                .isAccepted(false)
                .build();

        vacancy.setCandidates(List.of(candidate));
    }

    @Test
    void createVacancyValidDataShouldCreateVacancy() {

        VacancyCreateDto vacancyCreateDto = new VacancyCreateDto(
                1L,
                "Looking for experienced Java developer",
                TeamRole.DEVELOPER,
                3,
                "Java Developer"
        );
        Project project1 = projectRepository.getByIdOrThrow(vacancyCreateDto.projectId());
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(author);
        when(vacancyMapper.toVacancy(vacancyCreateDto, project1))
                .thenAnswer(invocation -> {
                    return Vacancy.builder()
                            .name(vacancyCreateDto.name())
                            .description(vacancyCreateDto.description())
                            .position(vacancyCreateDto.position())
                            .count(vacancyCreateDto.count())
                            .project(project)
                            .candidates(new ArrayList<>())
                            .acceptedCandidates(new ArrayList<>())
                            .build();
                });
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);

        VacancyDto result = vacancyService.createVacancy(vacancyCreateDto);

        assertNotNull(result);
        assertEquals(vacancy.getName(), result.name());
        verify(vacancyRepository).save(vacancyCaptor.capture());
        assertEquals(vacancyCreateDto.name(), vacancyCaptor.getValue().getName());
    }

    @Test
    void updateVacancyValidDataShouldUpdateVacancy() {

        VacancyUpdateDto updateDto = new VacancyUpdateDto(
                "Updated Java Developer",
                "Updated description",
                TeamRole.DEVELOPER,
                5
        );

        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(author);
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);

        VacancyDto result = vacancyService.updateVacancy(1L, updateDto);

        assertNotNull(result);
        verify(vacancyRepository).save(vacancyCaptor.capture());
        assertEquals(updateDto.name(), vacancyCaptor.getValue().getName());
        assertEquals(updateDto.description(), vacancyCaptor.getValue().getDescription());
    }

    @Test
    void addCandidatesToVacancyValidDataShouldAddCandidate() {

        CandidateCreateDto candidateDto = new CandidateCreateDto(
                20L,
                "candidate2",
                "resume123",
                "Cover letter"
        );

        Candidate newCandidate = Candidate.builder()
                .userId(20L)
                .username("candidate2")
                .resumeDocKey("resume123")
                .coverLetter("Cover letter")
                .build();

        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(author);
        when(candidateMapper.toCandidate(candidateDto)).thenReturn(newCandidate);
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);

        VacancyDto result = vacancyService.addCandidatesToVacancy(1L, candidateDto);

        assertNotNull(result);
        verify(vacancyRepository).save(vacancyCaptor.capture());
        assertEquals(2, vacancyCaptor.getValue().getCandidates().size());
    }

    @Test
    void updateCandidateStatusValidDataShouldUpdateStatus() {

        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(author);
        when(candidateRepository.findByVacancyIdAndCandidateIdOrThrow(1L, 200L)).thenReturn(candidate);

        CandidateDto result = vacancyService.updateCandidateStatus(1L, 200L, CandidateStatus.ACCEPTED);

        assertNotNull(result);
        assertEquals(CandidateStatus.ACCEPTED, result.candidateStatus());
        assertTrue(result.isAccepted());
    }

    @Test
    void closeVacancyValidDataShouldCloseVacancy() {
        List<Candidate> acceptedCandidate = new ArrayList<>();
        for (int i = 0; i < vacancy.getCount(); i++) {
            Candidate acceptCandidate = Candidate.builder()
                    .id((long) (i + 1))
                    .userId(200L + i)
                    .username("candidate" + i)
                    .candidateStatus(CandidateStatus.ACCEPTED)
                    .isAccepted(true)
                    .vacancy(vacancy)
                    .build();
            acceptedCandidate.add(acceptCandidate);
        }

        vacancy.setAcceptedCandidates(acceptedCandidate);
        vacancy.setStatus(VacancyStatus.OPEN);

        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(author);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(new VacancyDto(
                1L, "Java Developer", null, null, 3, VacancyStatus.OPEN, null
        ));

        VacancyDto result = vacancyService.closeVacancy(1L);

        assertNotNull(result);
        verify(vacancyRepository).save(vacancyCaptor.capture());
        assertEquals(vacancyCaptor.getValue().getStatus(), VacancyStatus.CLOSED);
    }

    @Test
    void getVacancyExistingIdShouldReturnVacancy() {

        when(vacancyRepository.getWithCandidatesOrThrow(1L)).thenReturn(vacancy);

        VacancyDto result = vacancyService.getVacancy(1L);

        assertNotNull(result);
        assertEquals(vacancy.getName(), result.name());
    }

    @Test
    void findVacanciesWithFiltersShouldReturnFilteredVacancies() {

        SearchDto searchDto = SearchDto.builder()
                .description("Java")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        when(vacancyRepository.findAll(any(Example.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(vacancy)));

        PageResponse<VacancyDto> result = vacancyService.findVacancies(pageable, searchDto);

        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(vacancy.getName(), result.content().get(0).name());
    }

    @Test
    void deleteVacancyValidIdShouldDeleteVacancy() {

        when(vacancyRepository.getByIdOrThrow(1L)).thenReturn(vacancy);
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(author);

        vacancyService.deleteVacancy(1L);

        verify(vacancyRepository).deleteById(1L);
    }
}