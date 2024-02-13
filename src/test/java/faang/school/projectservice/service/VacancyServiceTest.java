package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.CandidateStatus.ACCEPTED;
import static faang.school.projectservice.model.CandidateStatus.REJECTED;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static faang.school.projectservice.model.VacancyStatus.CLOSED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);
    @InjectMocks
    private VacancyService vacancyService;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private PrintStream printStream;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Captor
    private ArgumentCaptor<Vacancy> captorVacancy;

    @Test
    void testCreateVacancySuccessful() {
        Long createdBy = 11L;
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(1L)
                .projectId(2L)
                .build();
        Project project = Project.builder()
                .id(vacancyDto.getProjectId())
                .build();
        TeamMember member = new TeamMember();
        Vacancy vacancy = Vacancy.builder()
                .name(vacancyDto.getName())
                .createdBy(createdBy)
                .description("junior-dev")
                .status(VacancyStatus.OPEN)
                .count(5)
                .build();

        when(projectRepository.getProjectById(vacancyDto.getProjectId()))
                .thenReturn(project);
        when(teamMemberRepository.findById(createdBy))
                .thenReturn(member);
        vacancyService.createVacancy(vacancyDto, createdBy);

        verify(projectRepository).save(project);
        verify(vacancyRepository).save(captorVacancy.capture());
        assertEquals(vacancy, captorVacancy.getValue());
        assertEquals(1, member.getRoles().size());
        assertEquals(OWNER, member.getRoles().get(0));
    }

    @Test
    void testUpdateVacancyFailure() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(1L)
                .build();
        Vacancy vacancy = Vacancy.builder()
                .candidates(List.of(
                        new Candidate(),
                        new Candidate(),
                        new Candidate()))
                .count(5)
                .build();
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        System.setOut(printStream);
        vacancyService.updateVacancy(vacancyDto);
        verify(printStream).println("Нужно больше кандидатов");
    }

    @Test
    void testUpdateVacancySuccessful() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(1L)
                .build();
        Vacancy vacancy = Vacancy.builder()
                .candidates(List.of(
                        new Candidate(),
                        new Candidate(),
                        new Candidate()))
                .count(2)
                .build();
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        vacancyService.updateVacancy(vacancyDto);
        assertEquals(ACCEPTED, vacancy.getCandidates().get(0).getCandidateStatus());
        assertEquals(CLOSED, vacancy.getStatus());
    }

    @Test
    void testDeleteVacancySuccessful() {
        Candidate candidate1 = Candidate.builder()
                .id(11L)
                .userId(22L)
                .candidateStatus(ACCEPTED)
                .build();
        Candidate candidate2 = Candidate.builder()
                .id(12L)
                .userId(23L)
                .candidateStatus(REJECTED)
                .build();

        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .candidates(List.of(candidate1, candidate2))
                .build();

        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        vacancyService.deleteVacancy(1L);
        verify(vacancyRepository).deleteById(23L);
        verify(vacancyRepository, never()).deleteById(22L);
    }

    @Test
    void testGetVacancyWithAllFiltersSuccessful() {
        String nameTrue = "Java";
        String positionTrue = "Junior";
        String nameFalse = "Kotlin";
        String positionFalse = "Midl";
        Vacancy vacancy1 = getVacancy(1L, nameTrue, positionTrue, 11l);
        Vacancy vacancy2 = getVacancy(2L, nameFalse, positionTrue, 12l);
        Vacancy vacancy3 = getVacancy(3L, nameTrue, positionFalse, 13l);
        Vacancy vacancy4 = getVacancy(4L, nameFalse, positionFalse, 14l);
        List<Vacancy> list = List.of(vacancy1, vacancy2, vacancy3, vacancy4);
        when(vacancyRepository.findAll()).thenReturn(list);
        List<VacancyDto> vacancyDto = vacancyService.getVacanciesWithFilters(nameTrue, positionTrue);

        assertEquals(1, vacancyDto.size());
        assertEquals(vacancy1.getId(), list.get(0).getId());
    }

    @Test
    void testGetVacancyDataValidateException() {
        when(vacancyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> vacancyService.getVacancy(1L));
    }

    private static Vacancy getVacancy(Long id,
                                      String name,
                                      String description,
                                      Long projectId) {
        return Vacancy.builder()
                .id(id)
                .name(name)
                .description(description)
                .project(Project.builder()
                        .id(projectId)
                        .build())
                .build();
    }
}
