package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mappper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyFilters.VacancyFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Mock
    TeamMemberRepository teamMemberRepository;

    @Mock
    VacancyRepository vacancyRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    VacancyMapper vacancyMapper;

    @Mock
    List<VacancyFilter> filters;

    @InjectMocks
    VacancyService vacancyService;

    VacancyDto vacancyDto;

    @Nested
    class NegativeTestGroupA {
        @BeforeEach
        public void setUp() {
            vacancyDto = new VacancyDto(1L, "A", 1L, 1L, 1L, VacancyStatus.OPEN);
            Mockito.when(teamMemberRepository.findById(1L)).thenReturn(TeamMember
                    .builder()
                    .roles(List.of(TeamRole.DEVELOPER))
                    .build());
        }

        @Test
        public void testCreateVacancyThrowExcForProjectId() {
            DataValidationException e = assertThrows(DataValidationException.class,
                    () -> vacancyService.createVacancy(vacancyDto));
            assertEquals("There is no project with this id", e.getMessage());
        }

        @Test
        public void testCreateVacancyThrowExcForTeamRole() {
            Mockito.when(projectRepository.existsById(1L)).thenReturn(true);

            DataValidationException e = assertThrows(DataValidationException.class,
                    () -> vacancyService.createVacancy(vacancyDto));
            assertEquals("The vacancy creator doesn't have the required role", e.getMessage());
        }
    }

    @Nested
    class PositiveTestGroupA {
        @BeforeEach
        public void setUp() {
            vacancyDto = new VacancyDto(1L, "A", 1L, 1L, 1L, VacancyStatus.OPEN);

            Mockito.when(teamMemberRepository.findById(1L)).thenReturn(TeamMember
                    .builder()
                    .roles(List.of(TeamRole.OWNER))
                    .build());
            Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
            Mockito.when(vacancyMapper.toModel(vacancyDto)).thenReturn(new Vacancy());

            vacancyService.createVacancy(vacancyDto);
        }

        @Test
        public void testCreateVacancyCallFindById() {
            Mockito.verify(teamMemberRepository).findById(1L);
        }

        @Test
        public void testCreateVacancyCallExistsById() {
            Mockito.verify(projectRepository).existsById(1L);
        }

        @Test
        public void testCreateVacancyCallSave() {
            Mockito.verify(vacancyRepository).save(Mockito.any());
        }

        @Test
        public void testCreateVacancyCallToMapper() {
            Mockito.verify(vacancyMapper).toModel(vacancyDto);
            Mockito.verify(vacancyMapper).toDto(Mockito.any());
        }
    }

    @Nested
    class NegativeTestsGroupB {
        @BeforeEach
        public void setUp() {
            vacancyDto = new VacancyDto(1L, "A", 1L, 1L, 1L, VacancyStatus.CLOSED);
        }

        @Test
        public void testUpdateVacancyThrowEntityExcForId() {
            assertThrows(EntityNotFoundException.class, () -> vacancyService.updateVacancy(vacancyDto));
        }

        @Test
        public void testUpdateVacancyThrowIllegalArgsExc() {
            Vacancy vacancy = Vacancy.builder().candidates(List.of(new Candidate())).build();
            Mockito.when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

            assertThrows(IllegalArgumentException.class, () -> vacancyService.updateVacancy(vacancyDto));
        }
    }

    @Nested
    class PositiveTestsGroupB {
        @Mock
        Vacancy vacancy;

        @BeforeEach
        public void setUp() {
            vacancyDto = new VacancyDto(1L, "A", 1L, 1L, 1L, VacancyStatus.CLOSED);
            List<Candidate> candidates = List.of(new Candidate(), new Candidate(), new Candidate(), new Candidate(),
                    new Candidate());

            Mockito.when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
            Mockito.when(vacancy.getCandidates()).thenReturn(candidates);
            Mockito.when(teamMemberRepository.findById(1L)).thenReturn(TeamMember
                    .builder()
                    .roles(List.of(TeamRole.OWNER))
                    .build());
            Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
            Mockito.when(vacancyMapper.toModel(vacancyDto)).thenReturn(new Vacancy());

            vacancyService.updateVacancy(vacancyDto);
        }

        @Test
        public void testUpdateVacancyCallFindById() {
            Mockito.verify(vacancyRepository).findById(1L);
        }

        @Test
        public void testUpdateVacancyCallGetCandidates() {
            Mockito.verify(vacancy).getCandidates();
        }
    }

    @Nested
    class PositiveTestsGroupC {
        @Mock
        Vacancy vacancy;

        @BeforeEach
        public void setUp() {
            Mockito.when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
            Mockito.when(vacancy.getCandidates()).thenReturn(new ArrayList<>());
            vacancyService.deleteVacancy(1);
        }

        @Test
        public void testDeleteVacancyCallGetCandidates() {
            Mockito.verify(vacancy).getCandidates();
        }

        @Test
        public void testDeleteVacancyCallDeleteById() {
            Mockito.verify(vacancyRepository).deleteById(1L);
        }
    }

    @Nested
    class NegativeTestsGroupD {
        @Test
        public void testGetVacanciesReturnEmptyList() {
            List<VacancyDto> result = vacancyService.getVacancies(new VacancyFilterDto());
            assertEquals(Collections.emptyList(), result);
        }
    }

    @Nested
    class PositiveTestsGroupD {
        @BeforeEach
        public void setUp() {
            Mockito.when(vacancyRepository.findAll()).thenReturn(List.of(new Vacancy()));
            vacancyService.getVacancies(new VacancyFilterDto());
        }

        @Test
        public void testGetVacanciesCallFindAll() {
            Mockito.verify(vacancyRepository).findAll();
        }

        @Test
        public void testGetVacanciesCallStream() {
            Mockito.verify(filters).stream();
        }
    }
}