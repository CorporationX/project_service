package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.filter.VacancyFilterService;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @InjectMocks
    private VacancyService vacancyService;
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
        candidate.setVacancy(vacancy);

        vacancy.setCandidates(List.of(candidate));

        teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.INTERN));

        vacancyFilterDto = new VacancyFilterDto();
    }

    @Test
    public void testCreateVacancy() {
        vacancyService.createVacancy(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1))
                .save(vacancyMapper.toEntity(vacancyDto));
    }
    @Test
    public void testUpdateVacancy(){
        Mockito.doNothing().when(vacancyValidator).checkExistsVacancy(vacancyDto);
        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId())).thenReturn(vacancy);
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        vacancyService.updateVacancy(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancy);
    }

    @Test
    public void testUpdateVacancyIfClosed(){
        vacancy.setStatus(VacancyStatus.CLOSED);
        Mockito.doNothing().when(vacancyValidator).checkExistsVacancy(vacancyDto);
        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId())).thenReturn(vacancy);
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        vacancyService.updateVacancy(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancy);
    }

    @Test
    public void testDeleteVacancy() {
        List<Long> candidatesIds = List.of(1L);
        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId())).thenReturn(vacancy);
        vacancyService.deleteVacancy(vacancyDto);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).deleteAllById(candidatesIds);
        Mockito.verify(vacancyRepository, Mockito.times(1)).deleteById(vacancyDto.getId());
    }

    @Test
    public void testGetVacancyByNameAndPosition() {
        Mockito.when(vacancyRepository.findAll()).thenReturn(List.of(vacancy));
        vacancyService.getVacanciesWithFilter(vacancyFilterDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetVacancy() {
        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId())).thenReturn(vacancy);
        vacancyService.getVacancy(vacancyDto.getId());
        Mockito.verify(vacancyRepository, Mockito.times(1)).getReferenceById(vacancyDto.getId());
    }
}
