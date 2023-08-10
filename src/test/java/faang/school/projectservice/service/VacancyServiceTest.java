package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.IllegalCandidatesNumberException;
import faang.school.projectservice.mappper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyFilters.VacancyFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Mock
    VacancyRepository vacancyRepository;

    @Mock
    TeamMemberService teamMemberService;

    @Mock
    ProjectService projectService;

    @Mock
    VacancyMapper vacancyMapper;

    @InjectMocks
    VacancyService vacancyService;

    TeamMember teamMember1;
    TeamMember teamMember2;
    VacancyDto vacancyDto;
    Vacancy vacancy1;
    Vacancy vacancy2;
    Vacancy vacancy3;

    @BeforeEach
    public void setUp() {
        teamMember1 = TeamMember.builder()
                .roles(List.of(TeamRole.OWNER))
                .build();
        teamMember2 = TeamMember.builder()
                .roles(new ArrayList<>())
                .build();
        vacancyDto = VacancyDto.builder()
                .id(1L)
                .status(VacancyStatus.CLOSED)
                .build();
        vacancy1 = Vacancy.builder()
                .candidates(List.of(new Candidate()))
                .project(new Project())
                .build();
        vacancy2 = Vacancy.builder()
                .project(new Project())
                .build();
        vacancy3 = Vacancy.builder()
                .candidates(List.of(new Candidate(), new Candidate(), new Candidate(), new Candidate(),
                        new Candidate()))
                .build();
    }

    @Test
    public void testCreateVacancyThrowDataExc1() {
        Mockito.when(teamMemberService.findById(null)).thenReturn(teamMember1);

        DataValidationException exc = assertThrows(DataValidationException.class,
                () -> vacancyService.createVacancy(new VacancyDto()));
        assertEquals("There is no project with this id", exc.getMessage());
    }

    @Test
    public void testCreateVacancyThrowDataExc2() {
        Mockito.when(projectService.isProjectExist(Mockito.any())).thenReturn(true);
        Mockito.when(teamMemberService.findById(null)).thenReturn(teamMember2);

        DataValidationException exc = assertThrows(DataValidationException.class,
                () -> vacancyService.createVacancy(new VacancyDto()));
        assertEquals("The vacancy creator doesn't have the required role", exc.getMessage());
    }

    @Test
    public void testCreateVacancyCallMethods() {
        Mockito.when(projectService.isProjectExist(Mockito.any())).thenReturn(true);
        Mockito.when(teamMemberService.findById(null)).thenReturn(teamMember1);
        Mockito.when(vacancyMapper.toModel(new VacancyDto())).thenReturn(new Vacancy());
        Mockito.when(projectService.getProjectByIdFromRepo(null)).thenReturn(new Project());
        Mockito.when(vacancyRepository.save(vacancy2)).thenReturn(new Vacancy());
        vacancyService.createVacancy(new VacancyDto());

        Mockito.verify(teamMemberService).findById(Mockito.any());
        Mockito.verify(projectService).isProjectExist(Mockito.any());
        Mockito.verify(vacancyMapper).toModel(Mockito.any());
        Mockito.verify(projectService).getProjectByIdFromRepo(Mockito.any());
        Mockito.verify(vacancyRepository).save(Mockito.any());
        Mockito.verify(vacancyMapper).toDto(Mockito.any());
    }

    @Test
    public void testUpdateVacancyThrowEntityExc() {
        assertThrows(EntityNotFoundException.class, () -> vacancyService.updateVacancy(vacancyDto));
    }

    @Test
    public void testUpdateVacancyThrowIllegalCandidateExc() {
        Mockito.when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy1));
        assertThrows(IllegalCandidatesNumberException.class, () -> vacancyService.updateVacancy(vacancyDto));
    }

    @Test
    public void testUpdateVacancyCallMethods() {
        Mockito.when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy3));
        Mockito.when(projectService.isProjectExist(Mockito.any())).thenReturn(true);
        Mockito.when(teamMemberService.findById(null)).thenReturn(teamMember1);
        Mockito.when(vacancyMapper.toModel(vacancyDto)).thenReturn(new Vacancy());
        Mockito.when(projectService.getProjectByIdFromRepo(null)).thenReturn(new Project());
        Mockito.when(vacancyRepository.save(vacancy2)).thenReturn(new Vacancy());
        vacancyService.updateVacancy(vacancyDto);

        Mockito.verify(vacancyRepository).findById(Mockito.any());
        Mockito.verify(teamMemberService).findById(Mockito.any());
        Mockito.verify(projectService).isProjectExist(Mockito.any());
        Mockito.verify(vacancyMapper).toModel(Mockito.any());
        Mockito.verify(projectService).getProjectByIdFromRepo(Mockito.any());
        Mockito.verify(vacancyRepository).save(Mockito.any());
        Mockito.verify(vacancyMapper).toDto(Mockito.any());
    }

    @Test
    public void testDeleteVacancyCallMethods() {
        Mockito.when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy1));
        Mockito.when(teamMemberService.findByUserIdAndProjectId(null, null)).thenReturn(teamMember1);
        vacancyService.deleteVacancy(1L);

        Mockito.verify(vacancyRepository).findById(Mockito.any());
        Mockito.verify(teamMemberService).findByUserIdAndProjectId(Mockito.any(), Mockito.any());
        Mockito.verify(vacancyRepository).deleteById(Mockito.any());
    }

    @Test
    public void testGetVacanciesCallMethods() {
        VacancyFilter filterMock = Mockito.mock(VacancyFilter.class);
        List<VacancyFilter> filters = List.of(filterMock);
        vacancyService = new VacancyService(vacancyRepository, teamMemberService, projectService, vacancyMapper,
                filters);
        vacancyService.getVacancies(new VacancyFilterDto());

        Mockito.verify(vacancyRepository).findAll();
    }

    @Test
    public void testGetVacancyById() {
        Mockito.when(vacancyRepository.findById(1L)).thenReturn(Optional.of(new Vacancy()));
        vacancyService.getVacancyById(1L);

        Mockito.verify(vacancyMapper).toDto(Mockito.any());
    }
}