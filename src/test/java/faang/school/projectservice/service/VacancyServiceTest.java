package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.filters.filters.VacancyFilter;
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
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
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

    @BeforeEach
    public void setUp() {
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
                .candidates(List.of(new Candidate(), new Candidate()))
                .build();
        vacancyDto = new VacancyDto();
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
    public void testCreate() {
        when(teamMemberRepository.findById(vacancyDto.getCreatedBy()))
                .thenReturn(teamMember);
        Map<String, String> result = vacancyService.create(vacancyDto);
        assertEquals(result, Map.of("message", "vacancy created", "status", HttpStatus.CREATED.toString()));
    }

    @Test
    public void testUpdate() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        Candidate candidate1 = new Candidate();
        Candidate candidate2 = new Candidate();
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate1));
        when(candidateRepository.findById(2L)).thenReturn(Optional.of(candidate2));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        Map<String, String> result = vacancyService.update(vacancyId,vacancyDto);
        assertEquals(result, Map.of("message", "vacancy updated", "status", HttpStatus.OK.toString()));
        verify(vacancyRepository,atLeastOnce()).save(vacancyMapper.toEntity(vacancyDto));
        verify(candidateRepository, atLeastOnce()).save(candidate1);
        verify(candidateRepository, atLeastOnce()).save(candidate2);
    }

}