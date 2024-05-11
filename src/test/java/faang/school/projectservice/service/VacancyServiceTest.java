package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.TeamMemberValidator;
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
    private TeamMemberValidator teamMemberValidator;
    @Mock
    private VacancyValidator vacancyValidator;
    @Spy
    private VacancyMapperImpl vacancyMapper;
    VacancyDto vacancyDto;
    Candidate candidate;

    @BeforeEach
    public void init() {
        vacancyDto = new VacancyDto();
        candidate = new Candidate();
    }

    @Test
    public void testCreateVacancy() {
        vacancyService.createVacancy(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1))
                .save(vacancyMapper.toEntity(vacancyDto));
    }

    @Test
    public void testCloseVacancy() {

        List<Candidate> candidates = List.of(candidate);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId()))
                .thenReturn(vacancy);
        Mockito.doNothing().when(vacancyValidator).checkExistsVacancy(vacancyDto);
        Mockito.when(vacancy.getCandidates()).thenReturn(candidates);
        vacancyService.closeVacancy(vacancyDto);
//        Mockito.verify(vacancyRepository, Mockito.times(1)).getReferenceById(vacancyDto.getId());
        Mockito.verify(vacancyRepository, Mockito.times(1))
                .save(vacancyMapper.toEntity(vacancyDto));
    }
}
