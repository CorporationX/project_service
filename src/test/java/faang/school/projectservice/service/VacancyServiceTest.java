package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    private VacancyDto vacancyDto;
    private Long vacancyId;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private VacancyMapper vacancyMapper;

    @Spy
    private VacancyValidator vacancyValidator;

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    @BeforeEach
    void setUp() {
        vacancyDto = new VacancyDto();
        vacancyId = 1L;
    }

    private void prepareData(String name, String description, int count) {
        vacancyDto.setName(name);
        vacancyDto.setDescription(description);
        vacancyDto.setCount(count);
    }

    @Test
    public void testCreateVacancyWithEmptyName() {
        prepareData("  ", null, 0);
        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> vacancyService.createVacancy(vacancyDto)
        );

        assertEquals("Vacancy cannot have less than 1 positions", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testCreateVacancyWithEmptyDescription() {
        prepareData("some name", " ", 0);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> vacancyService.createVacancy(vacancyDto)
        );

        assertEquals("Vacancy cannot have less than 1 positions", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testCreateVacancyWithZeroCount() {
        prepareData("some name", "some description", 0);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> vacancyService.createVacancy(vacancyDto)
        );

        assertEquals("Vacancy cannot have less than 1 positions", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testCreateVacancyByInvalidSupervisor() {
        prepareData("some name", "some description", 2);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.DESIGNER, TeamRole.INTERN));
        when(teamMemberRepository.findById(vacancyDto.getCreatedBy())).thenReturn(teamMember);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> vacancyService.createVacancy(vacancyDto)
        );

        assertEquals("Only owner or manager can create vacancies", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testCreateVacancyForNoExistingProject() {
        prepareData("some name", "some description", 2);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.OWNER, TeamRole.INTERN));
        when(teamMemberRepository.findById(vacancyDto.getCreatedBy())).thenReturn(teamMember);

        when(projectRepository.existsById(vacancyDto.getProjectId())).thenReturn(false);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> vacancyService.createVacancy(vacancyDto)
        );

        assertEquals("Project does not exist", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testCreateVacancySuccessfully() {
        prepareData("some name", "some description", 2);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.OWNER, TeamRole.INTERN));
        when(teamMemberRepository.findById(vacancyDto.getCreatedBy())).thenReturn(teamMember);

        when(projectRepository.existsById(vacancyDto.getProjectId())).thenReturn(true);

        Vacancy vacancy = new Vacancy();
        when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);

        vacancyService.createVacancy(vacancyDto);

        verify(vacancyRepository, times(1)).save(vacancy);
    }

    @Test
    public void testUpdateVacancyWithEmptyName() {
        prepareData("  ", null, 0);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> vacancyService.updateVacancy(vacancyId, vacancyDto)
        );

        assertEquals("Vacancy cannot have less than 1 positions", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testUpdateVacancyWithEmptyDescription() {
        prepareData("some name", " ", 0);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> vacancyService.updateVacancy(vacancyId, vacancyDto)
        );

        assertEquals("Vacancy cannot have less than 1 positions", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testUpdateVacancyWithZeroCount() {
        prepareData("some name", "some description", 0);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> vacancyService.updateVacancy(vacancyId, vacancyDto)
        );

        assertEquals("Vacancy cannot have less than 1 positions", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testUpdateVacancyByInvalidSupervisor() {
        prepareData("some name", "some description", 2);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.DESIGNER, TeamRole.INTERN));
        when(teamMemberRepository.findById(vacancyDto.getUpdatedBy())).thenReturn(teamMember);

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> vacancyService.updateVacancy(vacancyId, vacancyDto)
        );

        assertEquals("Only owner or manager can update vacancies", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testUpdateNotExistingVacancy() {
        prepareData("some name", "some description", 2);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.OWNER, TeamRole.INTERN));
        when(teamMemberRepository.findById(vacancyDto.getUpdatedBy())).thenReturn(teamMember);

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> vacancyService.updateVacancy(vacancyId, vacancyDto)
        );

        assertEquals("Vacancy does not exist", thrown.getMessage());

        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testUpdateVacancySuccessfully() {
        prepareData("some name", "some description", 2);
        vacancyDto.setStatus(VacancyStatus.CLOSED);

        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.OWNER, TeamRole.INTERN));
        when(teamMemberRepository.findById(vacancyDto.getUpdatedBy())).thenReturn(teamMember);

        Vacancy vacancyToUpdate = new Vacancy();
        vacancyToUpdate.setStatus(VacancyStatus.OPEN);

        Candidate candidate = new Candidate();
        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);

        vacancyToUpdate.setCandidates(List.of(candidate));
        vacancyToUpdate.setCount(1);

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancyToUpdate));

        vacancyService.updateVacancy(vacancyId, vacancyDto);

        verify(vacancyRepository, times(1)).save(vacancyToUpdate);
    }

    @Test
    public void testDeleteVacancySuccessfully() {
        when(vacancyRepository.existsById(vacancyId)).thenReturn(true);

        Vacancy vacancyToDelete = new Vacancy();
        Candidate candidate = new Candidate();
        candidate.setCandidateStatus(CandidateStatus.ACCEPTED);
        vacancyToDelete.setCandidates(List.of(candidate));

        when(vacancyRepository.getReferenceById(vacancyId)).thenReturn(vacancyToDelete);

        vacancyService.deleteVacancy(vacancyId);

        verify(vacancyRepository, times(1)).deleteById(vacancyId);
    }

    @Test
    public void testGetVacancyByIdWithNoExistingId() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> vacancyService.getVacancyById(vacancyId));
        assertEquals("Vacancy does not exist", thrown.getMessage());
    }
}
