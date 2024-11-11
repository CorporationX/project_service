package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private VacancyService vacancyService;

    private Vacancy vacancy;
    private VacancyDto vacancyDto;
    private CreateVacancyDto createVacancyDto;
    private TeamMember curator;
    private Project project;

    @BeforeEach
    public void setup() {
        project = Project.builder().id(1L).build();
        curator = TeamMember.builder().id(1L).roles(List.of(TeamRole.OWNER)).build();

        vacancy = Vacancy.builder()
                .id(1L)
                .name("Senior Developer")
                .project(project)
                .build();

        vacancyDto = VacancyDto.builder()
                .id(1L)
                .name("Senior Developer")
                .projectId(1L)
                .build();

        createVacancyDto = CreateVacancyDto.builder()
                .name("Senior Developer")
                .createdBy(1L)
                .projectId(1L)
                .build();
    }

    @Nested
    @DisplayName("Создание вакансии")
    class CreateVacancyTests {

        @Test
        @DisplayName("Должен успешно создать вакансию")
        void shouldCreateVacancy() {
            when(vacancyMapper.toEntity(any(CreateVacancyDto.class))).thenReturn(vacancy);
            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(curator));
            when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
            when(vacancyMapper.toVacancyDto(any(Vacancy.class))).thenReturn(vacancyDto);

            VacancyDto createdVacancy = vacancyService.createVacancy(createVacancyDto);

            assertEquals("Senior Developer", createdVacancy.getName());
            verify(vacancyRepository).save(any(Vacancy.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение, если куратор не имеет роли OWNER")
        void shouldThrowExceptionWhenCuratorHasNoOwnerRole() {
            curator.setRoles(Collections.singletonList(TeamRole.DEVELOPER));
            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(curator));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                vacancyService.createVacancy(createVacancyDto);
            });

            assertEquals("Curator 1 does not have the required OWNER role", exception.getMessage());
            verify(vacancyRepository, never()).save(any(Vacancy.class));
        }
    }

    @Nested
    @DisplayName("Обновление вакансии")
    class UpdateVacancyTests {

        @Test
        @DisplayName("Должен успешно обновить вакансию и назначить роли кандидатам")
        void shouldUpdateVacancyAndAssignRoles() {
            Candidate candidate1 = new Candidate();
            candidate1.setUserId(1L);

            Candidate candidate2 = new Candidate();
            candidate2.setUserId(2L);

            vacancy.setCandidates(List.of(candidate1, candidate2));
            vacancy.setCount(2);

            Project project = Project.builder().id(1L).build();
            Team team = Team.builder().project(project).build();

            when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(vacancyMapper.toVacancyDto(any(Vacancy.class))).thenReturn(vacancyDto);
            when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);

            TeamMember teamMember1 = TeamMember.builder().id(1L).team(team).roles(new ArrayList<>()).build();
            TeamMember teamMember2 = TeamMember.builder().id(2L).team(team).roles(new ArrayList<>()).build();

            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember1));
            when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(teamMember2));

            vacancyDto.setStatus(VacancyStatus.CLOSED);
            VacancyDto updatedVacancy = vacancyService.updateVacancy(1L, vacancyDto);

            assertEquals("Senior Developer", updatedVacancy.getName());
            verify(teamMemberRepository, times(2)).save(any(TeamMember.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение, если недостаточно кандидатов")
        void shouldThrowExceptionIfNotEnoughCandidates() {
            Candidate candidate = new Candidate();
            candidate.setUserId(1L);

            vacancy.setCandidates(Collections.singletonList(candidate));
            vacancy.setCount(2);

            when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

            vacancyDto.setStatus(VacancyStatus.CLOSED);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                vacancyService.updateVacancy(1L, vacancyDto);
            });

            assertEquals("Cannot close vacancy 1, not enough candidates selected", exception.getMessage());
            verify(vacancyRepository, never()).save(any(Vacancy.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение, если кандидат не является участником проекта")
        void shouldThrowExceptionIfCandidateNotInProject() {
            Candidate candidate = new Candidate();
            candidate.setId(3L);
            candidate.setUserId(1L);
            vacancy.setCandidates(List.of(candidate));
            vacancy.setCount(1);

            when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(TeamMember.builder().id(1L).team(Team.builder().project(Project.builder().id(2L).build()).build()).build()));

            vacancyDto.setStatus(VacancyStatus.CLOSED);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                vacancyService.updateVacancy(1L, vacancyDto);
            });

            assertEquals("Candidate 3 is not a member of the project 1", exception.getMessage());
            verify(vacancyRepository, never()).save(any(Vacancy.class));
        }
    }

    @Nested
    @DisplayName("Удаление вакансии")
    class DeleteVacancyTests {

        @Test
        @DisplayName("Должен удалить непринятых кандидатов и вакансию")
        void shouldDeleteNonAcceptedCandidatesAndVacancy() {
            Candidate candidate1 = new Candidate();
            candidate1.setId(1L);
            candidate1.setUserId(1L);
            candidate1.setCandidateStatus(CandidateStatus.WAITING_RESPONSE); 
            
            Candidate candidate2 = new Candidate();
            candidate2.setId(2L);
            candidate2.setUserId(2L);
            candidate2.setCandidateStatus(CandidateStatus.ACCEPTED);

            vacancy.setCandidates(List.of(candidate1, candidate2));

            when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

            vacancyService.deleteVacancy(1L);

            verify(candidateRepository).deleteAllById(List.of(1L));
            verify(vacancyRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Не должен удалять принятых кандидатов, но должен удалить вакансию")
        void shouldNotDeleteAcceptedCandidatesButDeleteVacancy() {
            Candidate candidate1 = new Candidate();
            candidate1.setUserId(1L);
            candidate1.setCandidateStatus(CandidateStatus.WAITING_RESPONSE); 
            vacancy.setCandidates(List.of(candidate1));

            when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

            vacancyService.deleteVacancy(1L);

            verify(candidateRepository, never()).deleteById(1L);
            verify(vacancyRepository).deleteById(1L);
        }
    }
}
