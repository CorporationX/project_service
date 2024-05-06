package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.vacancy.VacancyFilter;
import faang.school.projectservice.validation.VacancyValidation;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    private VacancyRepository vacancyRepository;
    private VacancyMapper vacancyMapper;
    private VacancyValidation vacancyValidation;
    private VacancyFilter vacancyFilter;
    private TeamMemberJpaRepository teamMemberJpaRepository;
    VacancyService vacancyService;

    @BeforeEach
    void setup() {
        vacancyRepository = Mockito.mock(VacancyRepository.class);
        vacancyMapper = Mockito.mock(VacancyMapper.class);
        vacancyValidation = Mockito.mock(VacancyValidation.class);
        vacancyFilter = Mockito.mock(VacancyFilter.class);
        teamMemberJpaRepository = Mockito.mock(TeamMemberJpaRepository.class);
        List<VacancyFilter> filters = List.of(vacancyFilter);
        vacancyService = new VacancyService(vacancyRepository, vacancyValidation, vacancyMapper, teamMemberJpaRepository, filters);
    }


    @Test
    public void testCreateVacancySaveSuccess() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();

        when(vacancyValidation.validationCreate(vacancyDto)).thenReturn(true);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        vacancyService.createVacancy(vacancyDto);

        Mockito.verify(vacancyValidation, Mockito.times(1)).validationCreate(vacancyDto);
        Mockito.verify(vacancyMapper, Mockito.times(1)).toEntity(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancy);
        Mockito.verify(vacancyMapper, Mockito.times(1)).toDto(vacancy);
    }

    @Test
    public void testCreateVacancySaveFailure() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();

        when(vacancyValidation.validationCreate(vacancyDto)).thenReturn(false);
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(vacancyRepository.save(vacancy)).thenThrow(new RuntimeException("Ошибка при сохранении вакансии "));

        Assertions.assertThrows(RuntimeException.class, () -> vacancyService.createVacancy(vacancyDto));

        Mockito.verify(vacancyValidation, Mockito.times(1)).validationCreate(vacancyDto);
        Mockito.verify(vacancyMapper, Mockito.times(1)).toEntity(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancy);
    }

    @Test
    public void testUpdateVacancy() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1l);
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setStatus(VacancyStatus.CLOSED);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.updateVacancy(vacancyDto);

        verify(vacancyRepository, times(1)).findById(vacancyDto.getId());
        verify(vacancyRepository, times(1)).save(vacancy);
        verify(vacancyMapper, times(1)).toDto(vacancy);
        verify(vacancyValidation, times(1)).validationVacancyClosed(vacancyDto);
        verify(vacancyValidation, times(1)).validationVacancyIfCandidateNeed(vacancyDto);

        assertEquals(vacancyDto, result);

    }

    @Test
    public void testGetVacancyById() {
        long vacancyId = 1L;
        Vacancy vacancy = new Vacancy();
        vacancy.setId(vacancyId);

        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(vacancyId);

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.getVacancyById(vacancyId);

        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(vacancyId);
        Mockito.verify(vacancyMapper, Mockito.times(1)).toDto(vacancy);

        Assert.assertEquals(vacancyDto, result);
    }

    @Test
    public void testGetVacancies() {

        VacancyFilterDto vacancyFilterDto = new VacancyFilterDto();
        vacancyFilterDto.setName("java developer");
        vacancyFilterDto.setStatus(VacancyStatus.OPEN);

        List<Vacancy> vacancies = List.of(new Vacancy(), new Vacancy());
        Stream<Vacancy> stream = vacancies.stream();
        List<VacancyDto> vacancyDtoList = List.of(new VacancyDto(), new VacancyDto());

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(vacancyFilter.isApplicable(vacancyFilterDto)).thenReturn(true);
        when(vacancyFilter.apply(Mockito.any(Stream.class), Mockito.any(VacancyFilterDto.class))).thenReturn(stream);
        when(vacancyMapper.toDto(Mockito.any(Vacancy.class))).thenReturn(new VacancyDto());

        List<VacancyDto> filterVacancies = vacancyService.getFilterVacancies(vacancyFilterDto);

        Mockito.verify(vacancyRepository, Mockito.times(1)).findAll();
        Mockito.verify(vacancyFilter, Mockito.times(1)).isApplicable(vacancyFilterDto);
        Mockito.verify(vacancyFilter, Mockito.times(1)).apply(Mockito.any(Stream.class), Mockito.any(VacancyFilterDto.class));
        Mockito.verify(vacancyMapper, Mockito.times(vacancies.size())).toDto(Mockito.any(Vacancy.class));

        assertEquals(vacancyDtoList, filterVacancies);
    }

    @Test
    public void testDelete() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();
        Project project = new Project();
        Team team = new Team();
        List<TeamRole> roles = List.of(TeamRole.DEVELOPER);
        TeamMember tM1 = new TeamMember();
        tM1.setId(1L);
        tM1.setRoles(roles);
        TeamMember tM2 = new TeamMember();
        tM1.setId(2L);
        TeamMember teamMember = new TeamMember();
        List<TeamMember> teamMembers = List.of(tM1, tM2);


        team.setTeamMembers(teamMembers);
        List<Team> teams = List.of(team);
        project.setTeams(teams);
        vacancy.setProject(project);
        Candidate candidate1 = new Candidate();
        candidate1.setId(1L);
        Candidate candidate2 = new Candidate();
        candidate1.setId(2L);
        List<Candidate> candidates = List.of(candidate1, candidate2);
        vacancy.setCandidates(candidates);

        List<TeamMember> exceptedList = List.of(tM2);

        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(vacancy)).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.deleteVacancy(vacancyDto);

        verify(vacancyRepository, times(1)).findById(vacancyDto.getId());
        verify(teamMemberJpaRepository, times(1)).deleteAll(exceptedList);
        verify(vacancyMapper, times(1)).toDto(vacancy);

    }
}
