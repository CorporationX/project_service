package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.Vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.Vacancy.ExtendedVacancyDto;
import faang.school.projectservice.dto.Vacancy.UpdateVacancyDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Spy
    private VacancyMapper vacancyMapper = VacancyMapper.INSTANCE;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private VacancyService vacancyService;

    //    Сценарии: 1.
    @Test
    void testCreate_CreatorIsNotProjectOwner() {
        Project vacancyProject = Project
                .builder()
                .id(1L)
                .ownerId(2L)
                .build();
        CreateVacancyDto vacancyDto = CreateVacancyDto
                .builder()
                .id(5L)
                .projectId(1L)
                .build();

        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectRepository.getProjectById(anyLong())).thenReturn(vacancyProject);
        when(userContext.getUserId()).thenReturn(3L);

        Assertions.assertThrows(DataValidException.class, () -> vacancyService.create(vacancyDto));
        Mockito.verify(vacancyRepository, Mockito.times(0)).save(any());
    }

    @Test
    void testCreate_saveEntityFromValidDto() {
//        VacancyMapperImpl vacancyMapper = new VacancyMapperImpl();
        Project vacancyProject = Project
                .builder()
                .id(1L)
                .ownerId(2L)
                .build();
        CreateVacancyDto vacancyDto = CreateVacancyDto
                .builder()
                .id(5L)
                .projectId(1L)
                .build();

        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectRepository.getProjectById(anyLong())).thenReturn(vacancyProject);
        when(userContext.getUserId()).thenReturn(2L);

        vacancyService.create(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(any());
    }

    @Test
    void testUpdate_NotAvailableClosedStatus() {
        UpdateVacancyDto vacancyDto = UpdateVacancyDto
                .builder()
                .id(1L)
                .candidateIds(new ArrayList<>(List.of(1L, 2L)))
                .count(3)
                .status(VacancyStatus.CLOSED)
                .build();

        when(candidateRepository.countAllByVacancyIdAndStatusAndId(anyLong(), any(CandidateStatus.class), anyList()))
                .thenReturn(2);

        Assertions.assertThrows(DataValidException.class, () -> vacancyService.update(vacancyDto));
        Mockito.verify(vacancyRepository, Mockito.times(0)).save(any());

    }

    @Test
    void testUpdate_ClosedStatusCanBeSet() {

        Candidate acceptedCandidate = Candidate
                .builder()
                .id(1L)
                .candidateStatus(CandidateStatus.ACCEPTED)
                .build();
        Candidate waitingResponseCandidate = Candidate
                .builder()
                .id(2L)
                .candidateStatus(CandidateStatus.WAITING_RESPONSE)
                .build();
        UpdateVacancyDto vacancyDto = UpdateVacancyDto
                .builder()
                .id(1L)
                .candidateIds(new ArrayList<>(List.of(1L, 2L)))
                .count(2)
                .status(VacancyStatus.CLOSED)
                .build();
        Vacancy vacancy = Vacancy
                .builder()
                .id(1L)
                .candidates(new ArrayList<>(List.of(acceptedCandidate, acceptedCandidate, waitingResponseCandidate)))
                .count(2)
                .status(VacancyStatus.OPEN)
                .build();

        when(candidateRepository.countAllByVacancyIdAndStatusAndId(anyLong(), any(CandidateStatus.class), anyList()))
                .thenReturn(2);
        when(vacancyRepository.findById(any())).thenReturn(Optional.of(vacancy));
        when(userContext.getUserId()).thenReturn(3L);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);


//                Vacancy result = vacancyRepository.save(vacancy);
//
//        Vacancy vacancyExpected = Vacancy
//                .builder()
//                .status(VacancyStatus.CLOSED)
//                .build();
//
//
//               assertEquals(vacancyExpected.getStatus(), result.getStatus());

        vacancyService.update(vacancyDto);

        Mockito.verify(vacancyRepository, Mockito.times(1)).save(any());

//        проверка полей в ентити
    }

    @Test
    void TestChangeCandidateStatus() {

    }
}