package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;

import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    private static final long PROJECT_ID = 1L;
    private static final long TEAM_ID = 1L;
    private static final String NAME_VACANCY = "TEST VACANCY NAME";
    private static final Long CREATOR_ID = 1L;
    private static final String PROJECT_NAME = "TEST PROJECT";
    private static final long VACANCY_ID_DTO = 1L;
    private static final long VACANCY_ID = 1L;
    private static final long CANDIDATE_ID = 1L;
    private static final int COUNT = 3;
    @Mock
    private VacancyValidator vacancyValidator;
    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private List<VacancyFilter> vacancyFilters;

    @InjectMocks
    private VacancyService vacancyService;

    VacancyDto vacancyDto;
    Vacancy vacancy;
    Candidate candidate;

    UpdateVacancyDto updateVacancyDto;

    @BeforeEach
    void init() {
        candidate = new Candidate();
        candidate.setId(1L);
        candidate.setUserId(3L);
        candidate.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
        vacancyDto = VacancyDto.builder()
                .description("Tst")
                .projectId(2L)
                .build();
        vacancy = Vacancy.builder().id(1L)
                .name("Test")
                .candidates(List.of(candidate))
                .build();

        updateVacancyDto = UpdateVacancyDto.builder()
                .name("Updated")
                .build();
    }

    @Test
    @DisplayName("Get vacancy by ID : Wrong ID")
    public void testGetVacancyByIdWrongId() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vacancyService.getVacancyById(VACANCY_ID));
        verifyNoMoreInteractions(vacancyMapper);
    }

    @Test
    @DisplayName("Get vacancy by ID : Success")
    public void testGetVacancyByIdOk() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));
        VacancyDto result = vacancyService.getVacancyById(VACANCY_ID);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Delete vacancy test: Wrong Id")
    public void testDeleteVacancyByIdWrongId(){
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> vacancyService.deleteVacancyById(VACANCY_ID));
        verifyNoMoreInteractions(teamMemberJpaRepository);
    }

    @Test
    @DisplayName("Delete vacancy test: OK")
    public void testDeleteByIdOk(){
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));
        vacancyService.deleteVacancyById(VACANCY_ID);
        verify(teamMemberJpaRepository, times(1)).deleteAllById(any());
        verify(vacancyRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Update vacancy test: OK")
    public void testUpdateVacancy() {
        when(vacancyRepository.findById(anyLong())).thenReturn(Optional.of(vacancy));
        when(vacancyRepository.save(any())).thenReturn(vacancy);
        VacancyDto result = vacancyService.updateVacancy(1L, updateVacancyDto);
        assertEquals("Updated", result.getName());
    }

    @Test
    @DisplayName("Create vacancy test: OK")
    public void testCreateVacancy() {
        doNothing().when(vacancyValidator).validateExistingOfProject(anyLong());
        doNothing().when(vacancyValidator).validateVacancyCreator(anyLong(), anyLong());
        vacancy = vacancyMapper.toEntity(vacancyDto);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        VacancyDto result = vacancyService.create(vacancyDto);
        assertEquals("Tst", result.getDescription());
    }
}
