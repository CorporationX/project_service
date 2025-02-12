package faang.school.projectservice.service;

import faang.school.projectservice.exseption.VacancyNotFoundException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyValidatorService validateService;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private VacancyService vacancyService;

    @Test
    void createVacancySuccessfulTest() {
        Vacancy vacancy = new Vacancy();
        Project project = new Project();
        Long userId = 1L;
        Long projectId = 1L;
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        vacancyService.createVacancy(vacancy, userId, projectId);

        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancy);
    }

    @Test
    void updateVacancySuccessfulTest() {
        Vacancy vacancy = new Vacancy();
        Optional<Vacancy> vacancyOptional = Optional.of(vacancy);
        Long userId = 1L;
        Long vacancyId = 1L;
        vacancy.setPosition(TeamRole.DEVELOPER);
        vacancy.setCount(5);
        vacancy.setStatus(VacancyStatus.POSTPONED);

        Mockito.when(vacancyRepository.findById(vacancyId)).thenReturn(vacancyOptional);

        vacancyService.updateVacancy(vacancy, vacancyId, userId);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancy);
    }

    @Test
    void addCandidateToVacancySuccessfulEaseTest() {
        Vacancy vacancy = new Vacancy();
        List<Candidate> vacancyCandidates = new ArrayList<>();
        vacancy.setCandidates(vacancyCandidates);
        functionalCandidateAdd(vacancy, 2);
    }

    @Test
    void addCandidateToVacancySuccessfulHardTest() {
        Vacancy vacancy = new Vacancy();
        List<Candidate> vacancyCandidates = new ArrayList<>();
        vacancyCandidates.add(new Candidate());
        vacancy.setCandidates(vacancyCandidates);
        functionalCandidateAdd(vacancy, 3);
    }

    private void functionalCandidateAdd(Vacancy vacancy, int expected) {
        Optional<Vacancy> vacancyOptional = Optional.of(vacancy);
        List<Long> candidateIds = List.of(1L, 2L);
        List<Candidate> candidates = new ArrayList<>();
        Candidate firstCandidate = new Candidate();
        firstCandidate.setId(1L);
        Candidate secondCandidate = new Candidate();
        secondCandidate.setId(2L);
        candidates.add(firstCandidate);
        candidates.add(secondCandidate);
        Long userId = 1L;
        Long vacancyId = 1L;
        Long projectId = 1L;

        ArgumentCaptor<Vacancy> vacancyCaptor = ArgumentCaptor.forClass(Vacancy.class);

        Mockito.when(vacancyRepository.findById(vacancyId)).thenReturn(vacancyOptional);
        Mockito.when(candidateRepository.findAllById(candidateIds)).thenReturn(candidates);

        vacancyService.addCandidatesToVacancy(candidateIds, vacancyId, projectId, userId);

        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancyCaptor.capture());
        Vacancy capturedVacancy = vacancyCaptor.getValue();

        Assertions.assertEquals(expected, capturedVacancy.getCandidates().size());
        Assertions.assertTrue(capturedVacancy.getCandidates().containsAll(candidates));
    }

    @Test
    void removeVacancySuccessfulTest() {
        Long vacancyId = 1L;
        Long userId = 1L;

        Mockito.when(vacancyRepository.existsById(vacancyId)).thenReturn(true);

        vacancyService.removeVacancy(vacancyId, userId);
        Mockito.verify(vacancyRepository, Mockito.times(1)).deleteById(vacancyId);
    }

    @Test
    void removeVacancyExceptionTest() {
        Long vacancyId = 1L;
        Long userId = 1L;

        Mockito.when(vacancyRepository.existsById(vacancyId)).thenReturn(false);

        Assertions.assertThrows(VacancyNotFoundException.class, () ->
                vacancyService.removeVacancy(vacancyId, userId));
        Mockito.verify(vacancyRepository, Mockito.times(0)).deleteById(vacancyId);
    }

    @Test
    void filterVacanciesSuccessfulTest() {
        TeamRole position = TeamRole.DEVELOPER;
        String name = "vacancy name";
        List<Vacancy> vacanciesAll = new ArrayList<>();
        List<Vacancy> vacanciesExpected = new ArrayList<>();
        Vacancy firstVacancy = new Vacancy();
        firstVacancy.setPosition(position);
        firstVacancy.setName(name);
        vacanciesAll.add(firstVacancy);
        Vacancy secondVacancy = new Vacancy();
        secondVacancy.setPosition(position);
        secondVacancy.setName(name);
        vacanciesAll.add(secondVacancy);
        Vacancy thirdVacancy = new Vacancy();
        thirdVacancy.setPosition(TeamRole.ANALYST);
        thirdVacancy.setName("Such Leute");
        vacanciesAll.add(thirdVacancy);
        vacanciesExpected.add(firstVacancy);
        vacanciesExpected.add(secondVacancy);

        Mockito.when(vacancyRepository.findAll()).thenReturn(vacanciesAll);

        Assertions.assertEquals(vacanciesExpected, vacancyService.filterVacancies(position, name));
    }

    @Test
    void filterVacanciesExceptionTest() {
        TeamRole position = TeamRole.DEVELOPER;
        String name = "vacancy name";
        List<Vacancy> vacanciesAll = new ArrayList<>();

        Mockito.when(vacancyRepository.findAll()).thenReturn(vacanciesAll);

        Assertions.assertThrows(VacancyNotFoundException.class, () ->
                vacancyService.filterVacancies(position, name));
    }

    @Test
    void getVacancyByIdTest() {
        Long vacancyId = 1L;

        Mockito.when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(new Vacancy()));

        vacancyService.getVacancyById(vacancyId);
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(vacancyId);
    }

}
