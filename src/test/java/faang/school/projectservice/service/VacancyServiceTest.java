package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private VacancyMapper vacancyMapper;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private ProjectService projectService;
    @Mock
    private List<Filter<VacancyFilterDto, Vacancy>> filters;
    @InjectMocks
    private VacancyService vacancyService;

    @Test
    @DisplayName("Test get vacancy by id")
    void testGetVacancyById() {
        long vacancyId = 1L;

        Vacancy vacancy = new Vacancy();
        vacancy.setId(vacancyId);

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        Vacancy result = vacancyService.getVacancyById(vacancyId);

        assertEquals(vacancy, result);
    }

    @Test
    @DisplayName("Test get vacancy by id return exception")
    void testGetVacancyById_EntityNotFoundException() {
        long vacancyId = 1L;
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacancyService.getVacancyById(vacancyId));
    }

    @Test
    @DisplayName("Test get vacancy dto")
    void testGetVacancyDto() {
        long vacancyId = 1L;
        Vacancy vacancy = new Vacancy();
        vacancy.setId(vacancyId);
        VacancyDto vacancyDto = new VacancyDto();
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.getVacancyDto(vacancyId);

        assertEquals(vacancyDto, result);
    }

    @Test
    @DisplayName("Test get vacancies")
    void testGetVacancies() {
        VacancyFilterDto filter = new VacancyFilterDto();

        Vacancy vacancy1 = new Vacancy();
        vacancy1.setId(1L);

        Vacancy vacancy2 = new Vacancy();
        vacancy1.setId(2L);

        List<Vacancy> vacancies = new ArrayList<>();
        vacancies.add(vacancy1);
        vacancies.add(vacancy2);

        when(vacancyRepository.findAll()).thenReturn(vacancies);

        when(vacancyMapper.toDto(any())).thenReturn(new VacancyDto());

        List<VacancyDto> result = vacancyService.getVacancies(filter);

        assertEquals(vacancies.size(), result.size());
    }

    @Test
    @DisplayName("Test create vacancy")
    void testCreateVacancy() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setProjectId(1L);
        vacancyDto.setCreatedBy(1L);

        Project project = new Project();
        project.setId(1L);

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setCreatedBy(1L);

        when(projectService.getProjectById(vacancyDto.getProjectId())).thenReturn(project);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(any())).thenReturn(vacancy);
        when(vacancyMapper.toDto(any())).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.createVacancy(vacancyDto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Test update vacancy")
    void testUpdateVacancy() {
        long id = 1L;

        Candidate candidate = new Candidate();
        candidate.setId(id);
        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);

        List<Candidate> candidates = List.of(candidate);

        List<TeamRole> roles = new ArrayList<>();

        TeamMember teamMember = new TeamMember();
        teamMember.setId(id);
        teamMember.setRoles(roles);

        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(id);

        Vacancy vacancy = new Vacancy();
        vacancy.setId(id);
        vacancy.setCandidates(candidates);

        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(teamMemberService.getTeamMember(id)).thenReturn(teamMember);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.updateVacancy(vacancyDto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Test close vacancy")
    void testCloseVacancy() {
        long vacancyId = 1L;
        Vacancy vacancy = new Vacancy();
        vacancy.setId(vacancyId);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCandidates(new ArrayList<>());
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(new VacancyDto());

        VacancyDto result = vacancyService.closeVacancy(vacancyId);

        assertNotNull(result);
        assertEquals(VacancyStatus.CLOSED, vacancy.getStatus());
    }

}
