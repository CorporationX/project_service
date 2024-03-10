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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.CandidateStatus.ACCEPTED;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static faang.school.projectservice.model.VacancyStatus.CLOSED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Test
    void testCreateVacancySuccessful() {
        Long createdBy = 11L;
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(1L)
                .projectId(2L)
                .description("test")
                .createdBy(createdBy)
                .count(5)
                .build();
        Project project = Project.builder()
                .id(vacancyDto.getProjectId())
                .build();
        TeamMember member = new TeamMember();
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .name(vacancyDto.getName())
                .createdBy(createdBy)
                .description("test")
                .status(VacancyStatus.OPEN)
                .count(5)
                .build();

        when(projectRepository.getProjectById(vacancyDto.getProjectId()))
                .thenReturn(project);
        when(teamMemberRepository.findById(createdBy))
                .thenReturn(member);
        vacancyService.createVacancy(vacancyDto);

        verify(projectRepository).save(project);
        verify(vacancyRepository).save(vacancy);
        assertEquals(1, member.getRoles().size());
        assertEquals(OWNER, member.getRoles().get(0));
    }

    @Test
    void testUpdateVacancySuccessful() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(1L)
                .build();
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
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
        when(vacancyRepository.findById(1L)).thenReturn(
                Optional.of(Vacancy.builder()
                        .candidates(
                                List.of(new Candidate(), new Candidate()))
                        .build()));
        vacancyService.deleteVacancy(1L);
        verify(teamMemberRepository, times(2)).deleteById(any());
        verify(vacancyRepository).deleteById(any());
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
