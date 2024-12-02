package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateDeleteVacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private CandidateService candidateService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private ProjectService projectService;

    @Mock
    private VacancyMapper vacancyMapper;

    @InjectMocks
    private VacancyService vacancyService;

    private CreateVacancyDto createVacancyDto;
    private UpdateDeleteVacancyDto updateVacancyDto;
    private Vacancy vacancy;
    private Project project;

    @BeforeEach
    public void setUp() {
        createVacancyDto = new CreateVacancyDto(1L, "Test Vacancy", "Description", 1L, List.of(1L), null, 1L, 5);
        updateVacancyDto = new UpdateDeleteVacancyDto(1L, "Test Vacancy", "Description", 1L, List.of(1L), null, 1L, 5);
        vacancy = new Vacancy();
        project = new Project();
    }

    @Test
    public void testCreateVacancy_Success() {
        // Arrange
        when(teamMemberService.curatorHasNoAccess(1L)).thenReturn(false);
        when(vacancyMapper.toEntity(createVacancyDto)).thenReturn(vacancy);
        when(projectService.findProjectById(1L)).thenReturn(project);
        when(vacancyMapper.toCreateDto(vacancy)).thenReturn(createVacancyDto);

        // Act
        CreateVacancyDto result = vacancyService.createVacancy(createVacancyDto);

        // Assert
        assertNotNull(result);
        verify(vacancyRepository, times(1)).save(vacancy);
    }

    @Test
    public void testCreateVacancy_NoCuratorAccess() {
        // Arrange
        when(teamMemberService.curatorHasNoAccess(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DataValidationException.class, () -> vacancyService.createVacancy(createVacancyDto));
        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testUpdateVacancy_Success() {
        // Arrange
        when(teamMemberService.curatorHasNoAccess(1L)).thenReturn(false);
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toUpdateDeleteDto(vacancy)).thenReturn(updateVacancyDto);

        // Act
        UpdateDeleteVacancyDto result = vacancyService.updateVacancy(updateVacancyDto);

        // Assert
        assertNotNull(result);
        verify(vacancyRepository, times(1)).save(vacancy);
    }

    @Test
    public void testUpdateVacancy_NoCuratorAccess() {
        // Arrange
        when(teamMemberService.curatorHasNoAccess(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(DataValidationException.class, () -> vacancyService.updateVacancy(updateVacancyDto));
        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }

    @Test
    public void testDeleteVacancy_Success() {
        // Arrange
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        vacancy.setCandidates(List.of(candidate));
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        // Act
        vacancyService.deleteVacancy(updateVacancyDto);

        // Assert
        verify(candidateService, times(1)).deleteCandidate(1L);
        verify(vacancyRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetFilteredVacancies() {
        // Arrange
        Vacancy vacancy1 = new Vacancy();
        vacancy1.setName("Test Vacancy 1");
        vacancy1.setDescription("Developer");

        Vacancy vacancy2 = new Vacancy();
        vacancy2.setName("Test Vacancy 2");
        vacancy2.setDescription("Tester");

        when(vacancyRepository.findAll()).thenReturn(List.of(vacancy1, vacancy2));
        when(vacancyMapper.toUpdateDeleteDto(anyList())).thenReturn(List.of(updateVacancyDto));

        // Act
        List<UpdateDeleteVacancyDto> result = vacancyService.getFilteredVacancies("Test Vacancy 1", "Developer");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetVacancy_Success() {
        // Arrange
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toUpdateDeleteDto(vacancy)).thenReturn(updateVacancyDto);

        // Act
        UpdateDeleteVacancyDto result = vacancyService.getVacancy(1L);

        // Assert
        assertNotNull(result);
        assertEquals(updateVacancyDto, result);
    }

    @Test
    public void testGetVacancy_NotFound() {
        // Arrange
        when(vacancyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataValidationException.class, () -> vacancyService.getVacancy(1L));
    }
}