package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filters.filters.VacancyFilter;
import faang.school.projectservice.filters.filters.VacancyNameFilter;
import faang.school.projectservice.filters.filters.VacancyStatusFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class VacancyServiceTest {
    @InjectMocks
    private VacancyService vacancyService;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private VacancyMapper vacancyMapper;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private List<VacancyFilter> vacancyFilters;
    private Long vacancyId;
    private Vacancy vacancy;
    private TeamMember teamMember;
    private VacancyDto vacancyDto;
    private VacancyNameFilter vacancyNameFilter;
    private VacancyStatusFilter vacancyStatusFilter;
    private VacancyFilterDto vacancyFilterDto;

    @BeforeEach
    public void setUp() {
        vacancyFilterDto = VacancyFilterDto
                .builder()
                .name("Java")
                .status(VacancyStatus.OPEN)
                .build();
        vacancyNameFilter = Mockito.mock(VacancyNameFilter.class);
        vacancyStatusFilter = Mockito.mock(VacancyStatusFilter.class);
        List<VacancyFilter> filters = List.of(vacancyNameFilter, vacancyStatusFilter);
        vacancyService = new VacancyService(
                vacancyRepository,
                teamMemberRepository,
                vacancyMapper,
                candidateRepository,
                filters
        );
        vacancyId = 1L;
        vacancy = Vacancy.builder()
                .id(vacancyId)
                .name("frontend dev")
                .description("Description")
                .project(new Project())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(1L)
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .salary(1000.0)
                .count(1)
                .requiredSkillIds(List.of(1L, 2L))
                .build();

        vacancy.setCandidates(getCandidates(vacancy));

        vacancyDto = VacancyDto.builder().build();
        vacancyDto.setId(vacancyId);
        vacancyDto.setName("frontend dev");
        vacancyDto.setDescription("Description");
        vacancyDto.setProjectId(1L);
        vacancyDto.setCreatedAt(LocalDateTime.now());
        vacancyDto.setUpdatedAt(LocalDateTime.now());
        vacancyDto.setCreatedBy(1L);
        vacancyDto.setUpdatedBy(1L);
        vacancyDto.setStatus(VacancyStatus.OPEN);
        vacancyDto.setSalary(1000.0);
        vacancyDto.setCount(1);
        vacancyDto.setRequiredSkillIds(List.of(1L, 2L));
        vacancyDto.setCandidateIds(List.of(1L, 2L));
        teamMember = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.MANAGER))
                .team(new Team())
                .stages(List.of(new Stage(), new Stage()))
                .build();
    }

    @Test
    public void testGetOne() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);
        VacancyDto result = vacancyService.getVacancy(vacancyId);
        assertNotNull(result);
        assertEquals(vacancyDto.getId(), result.getId());
    }

    @Test
    public void testGetOneThrowsException() {
        when(vacancyRepository.findById(vacancyId))
                .thenThrow(new EntityNotFoundException("Not found"));
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            vacancyService.getVacancy(vacancyId);
        });
        assertEquals("Not found", ex.getMessage());
    }


    @Test
    public void testCreate() {
        when(teamMemberRepository.findById(vacancyDto.getCreatedBy()))
                .thenReturn(teamMember);
        Map<String, String> result = vacancyService.create(vacancyDto);
        assertEquals(result, Map.of("message", "vacancy created", "status", HttpStatus.CREATED.toString()));
    }

    @Test
    public void testCreateThrowsNotFoundException() {
        when(teamMemberRepository.findById(1L))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Team member doesn't exist by id: " + 1L));
        jakarta.persistence.EntityNotFoundException ex = assertThrows(
                jakarta.persistence.EntityNotFoundException.class,
                () -> vacancyService.create(vacancyDto));
        assertEquals("Team member doesn't exist by id: " + 1L, ex.getMessage());
        verify(vacancyRepository, times(0)).save(vacancy);
    }

    @Test
    public void testUpdate() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        Candidate candidate1 = Candidate.builder().build();
        Candidate candidate2 = Candidate.builder().build();
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate1));
        when(candidateRepository.findById(2L)).thenReturn(Optional.of(candidate2));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        Map<String, String> result = vacancyService.update(vacancyId, vacancyDto);
        assertEquals(result, Map.of("message", "vacancy updated", "status", HttpStatus.OK.toString()));
    }

    @Test
    public void testDelete() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        Map<String, String> res = vacancyService.delete(vacancyId);
        assertEquals(res.get("message"), "vacancy deleted");
        verify(vacancyRepository, times(1)).delete(vacancy);

    }

    @Test
    public void getAll() {
        when(vacancyRepository.findAll()).thenReturn(getVacancies());
//        when(vacancyMapper.toDtoList(getFilteredVacancies(vacancyFilterDto)))
//                .thenReturn(getFilteredDtos());
        List<VacancyDto> result = vacancyService.getAll(vacancyFilterDto);

        assertEquals(result.size(),getFilteredVacancies(vacancyFilterDto).size());
    }
    private List<VacancyDto> getFilteredDtos(){
        VacancyDto firstDto = VacancyDto
                .builder()
                .id(1L)
                .name("Java developer")
                .description("Java developer to startup")
                .status(VacancyStatus.OPEN)
                .projectId(1L)
                .salary(1000.0)
                .build();

        return List.of(firstDto);
    }
    private List<Vacancy> getFilteredVacancies(VacancyFilterDto filter ) {
        return getVacancies()
                .stream().filter(el->
                el.getName().contains(filter.getName())&& el.getStatus().equals(filter.getStatus())).toList();
    }
    private List<Vacancy> getVacancies() {
        Vacancy firstVacancy = Vacancy
                .builder()
                .id(1L)
                .name("Java developer")
                .description("Java developer to startup")
                .status(VacancyStatus.OPEN)
                .project(new Project())
                .salary(1000.0)
                .build();
        Vacancy secondVacancy = Vacancy
                .builder()
                .id(1L)
                .name("Python developer")
                .description("Python developer to real estate project")
                .status(VacancyStatus.OPEN)
                .project(new Project())
                .salary(1000.0)
                .build();
        ArrayList<Vacancy> result = new ArrayList<>();
        result.add(firstVacancy);
        result.add(secondVacancy);
        return result;
    }

    private List<Candidate> getCandidates(Vacancy vacancy) {
        Candidate firstCandidate = Candidate
                .builder()
                .id(1L)
                .userId(1L)
                .resumeDocKey("123")
                .coverLetter("Hello")
                .candidateStatus(CandidateStatus.ACCEPTED)
                .vacancy(vacancy)
                .build();
        Candidate secondCandidate = Candidate
                .builder()
                .id(2L)
                .userId(2L)
                .resumeDocKey("1234")
                .coverLetter("Java dev")
                .candidateStatus(CandidateStatus.ACCEPTED)
                .vacancy(vacancy)
                .build();
        return List.of(firstCandidate, secondCandidate);
    }
}