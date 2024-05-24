package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.vacancy.filters.VacancyFilterService;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @InjectMocks
    private VacancyServiceImpl vacancyServiceImpl;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private VacancyFilterService vacancyFilterService;
    @Spy
    private VacancyMapperImpl vacancyMapper;
    @Captor
    private ArgumentCaptor<List<Long>> captorCandidatesIds;

    VacancyDto vacancyDto;
    Candidate candidate;
    Vacancy vacancy;
    TeamMember teamMember;
    VacancyFilterDto vacancyFilterDto;

    @BeforeEach
    public void init() {
        vacancyDto = VacancyDto.builder()
                .id(1L)
                .name("Google")
                .description("developer")
                .createdBy(1L)
                .projectId(1L)
                .candidatesIds(List.of(1L, 2L))
                .count(3)
                .build();

        vacancy = vacancyMapper.toEntity(vacancyDto);

        candidate = new Candidate();
        candidate.setId(1L);
        candidate.setCandidateStatus(CandidateStatus.REJECTED);
        candidate.setVacancy(vacancy);

        vacancy.setCandidates(List.of(candidate));

        teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.INTERN));

        vacancyFilterDto = new VacancyFilterDto();
    }

    @Test
    public void testCreateVacancy() {
        vacancyServiceImpl.createVacancy(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1))
                .save(vacancyMapper.toEntity(vacancyDto));
    }
    @Test
    public void testUpdateVacancy(){
        Mockito.doNothing().when(vacancyValidator).checkExistsVacancy(vacancyDto);
        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId())).thenReturn(vacancy);
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        vacancyServiceImpl.updateVacancy(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancy);
    }

    @Test
    public void testUpdateVacancyIfClosed(){
        vacancy.setStatus(VacancyStatus.CLOSED);
        Mockito.doNothing().when(vacancyValidator).checkExistsVacancy(vacancyDto);
        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId())).thenReturn(vacancy);
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        vacancyServiceImpl.updateVacancy(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancy);
    }

    @Test
    public void testDeleteVacancy() {
        List<Long> candidatesIds = List.of(1L);

        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId())).thenReturn(vacancy);
        Mockito.when(candidateRepository.getReferenceById(vacancyDto.getCandidatesIds().get(0))).thenReturn(candidate);
        Mockito.doNothing().when(teamMemberRepository).deleteAllById(candidatesIds);
        vacancyServiceImpl.deleteVacancy(vacancyDto);
        Mockito.verify(teamMemberRepository).deleteAllById(captorCandidatesIds.capture());
        List<Long> actuallyIds = captorCandidatesIds.getValue();
        assertEquals(candidatesIds, actuallyIds);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).deleteAllById(candidatesIds);
        Mockito.verify(vacancyRepository, Mockito.times(1)).deleteById(vacancyDto.getId());
    }

    @Test
    public void testGetVacancyByNameAndPosition() {
        Mockito.when(vacancyRepository.findAll()).thenReturn(List.of(vacancy));
        vacancyServiceImpl.getVacanciesWithFilter(vacancyFilterDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetVacancy() {
        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId())).thenReturn(vacancy);
        vacancyServiceImpl.getVacancy(vacancyDto.getId());
        Mockito.verify(vacancyRepository, Mockito.times(1)).getReferenceById(vacancyDto.getId());
    }
}
