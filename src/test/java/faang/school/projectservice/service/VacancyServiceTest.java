package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @InjectMocks
    private VacancyService vacancyService;

    @Mock
    private VacancyMapper vacancyMapper;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private VacancyServiceValidator vacancyServiceValidator;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    private Vacancy vacancy;
    private VacancyDto vacancyDto;
    private long vacancyId;
    private TeamMember teamMemberOwner;
    private TeamMember teamMember;
    private Candidate candidate;
    private Optional<Vacancy> optionalVacancy;

    @BeforeEach
    public void setUp() {
        vacancyId = 1L;
        candidate = new Candidate();
        candidate.setId(1L);
        candidate.setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
        teamMember = TeamMember.builder()
                .id(2L)
                .roles(new ArrayList<>())
                .build();

        teamMemberOwner = TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();

        vacancy = Vacancy.builder()
                .candidates(List.of(candidate))
                .id(vacancyId)
                .build();

        vacancyDto = VacancyDto.builder()
                .projectId(1L)
                .ownerId(1L)
                .count(5)
                .build();

        optionalVacancy = Optional.of(vacancy);
    }

    @DisplayName("Метод создания вакансии отрабатывает")
    @Test
    public void testCreateVacancyWhenValid() {
        when(teamMemberRepository.findById(vacancyDto.getOwnerId())).thenReturn(teamMemberOwner);
        when(vacancyMapper.toVacancy(vacancyDto)).thenReturn(vacancy);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        vacancyService.createVacancy(vacancyDto);

        verify(vacancyRepository, times(1)).save(vacancy);
        verify(vacancyServiceValidator, times(1)).existByProject(vacancyDto.getProjectId());
        verify(vacancyServiceValidator, times(1)).ensureTeamRoleOwner(teamMemberOwner);
    }

    @DisplayName("Метод обновления вакансии отрабатывает")
    @Test
    public void testUpdateVacancyWhenValid() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(optionalVacancy);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        vacancyService.updateVacancy(vacancyDto, vacancyId);

        verify(vacancyServiceValidator, times(1)).existByVacancy(optionalVacancy);
        verify(vacancyMapper, times(1)).updateVacancy(vacancyDto, vacancy);
        verify(vacancyRepository, times(1)).save(vacancy);
        verify(vacancyMapper, times(1)).toVacancyDto(vacancy);
    }

    @DisplayName("Метод закрытия вакансии отрабатывает")
    @Test
    public void testCloseVacancyWhenValid() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(optionalVacancy);
        when(teamMemberRepository.findById(candidate.getId())).thenReturn(teamMember);

        vacancyService.closeVacancy(vacancyId);

        verify(vacancyServiceValidator, times(1)).existByVacancy(optionalVacancy);
        verify(vacancyServiceValidator, times(1)).checkCountCandidatesToClose(vacancy);
        verify(vacancyRepository, times(1)).save(vacancy);
    }

    @DisplayName("Метод удаления вакансии отрабатывает")
    @Test
    public void testDeleteVacancyWhenValid() {
        List<Long> candidateDeleteId = List.of(candidate.getId());
        when(vacancyRepository.findById(vacancyId)).thenReturn(optionalVacancy);

        vacancyService.deleteVacancy(vacancyId);

        verify(vacancyServiceValidator, times(1)).existByVacancy(optionalVacancy);
        verify(teamMemberRepository, times(1)).deleteTeamMember(candidateDeleteId);
        verify(vacancyRepository, times(1)).delete(vacancy);
    }

    @DisplayName("Метод получения вакансии по имени и позиции отрабатывает")
    @Test
    public void testGetVacancyPositionAndNameWhenValid() {
        VacancyFilterDto vacancyFilterDto = VacancyFilterDto.builder()
                .name("test")
                .status(VacancyStatus.OPEN)
                .build();
        when(vacancyRepository.findByFilter(vacancyFilterDto.getName(), vacancyFilterDto.getStatus()))
                .thenReturn(List.of(vacancy));
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        vacancyService.getVacancyPositionAndName(vacancyFilterDto);

        verify(vacancyRepository, times(1)).findByFilter(vacancyFilterDto.getName(), vacancyFilterDto.getStatus());
    }

    @DisplayName("Метод получения вакансии по id отрабатывает")
    @Test
    public void testGetVacancyWhenValid() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(optionalVacancy);
        when(vacancyMapper.toVacancyDto(vacancy)).thenReturn(vacancyDto);

        vacancyService.getVacancyById(vacancyId);

        verify(vacancyServiceValidator, times(1)).existByVacancy(optionalVacancy);
    }
}