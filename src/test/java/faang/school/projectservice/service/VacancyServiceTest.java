package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mappper.VacancyMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

    @InjectMocks
    VacancyService vacancyService;

    VacancyDto vacancyDto;

    @Nested
    class negativeTestGroup {
        @BeforeEach
        public void setUp() {
            vacancyDto = new VacancyDto(1L, "A", 1L, 1L, VacancyStatus.OPEN);
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
    class positiveTestGroup {
        @BeforeEach
        public void setUp() {
            vacancyDto = new VacancyDto(1L, "A", 1L, 1L, VacancyStatus.OPEN);

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
}