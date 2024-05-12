package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    Vacancy vacancy;
    TeamMember teamMember;

    @BeforeEach
    public void init() {
        vacancyDto = VacancyDto.builder()
                .id(1L)
                .name("Google")
//                .projectId(1L)
                .description("developer")
                .createdBy(1)
                .createdBy(1L)
                .build();

        vacancy = vacancyMapper.toEntity(vacancyDto);

        candidate = new Candidate();
        candidate.setId(1L);
        candidate.setVacancy(vacancy);

        vacancy.setCandidates(List.of(candidate));
    }

    @Test
    public void testCreateVacancy() {
        vacancyService.createVacancy(vacancyDto);
        Mockito.verify(vacancyRepository, Mockito.times(1))
                .save(vacancyMapper.toEntity(vacancyDto));
    }

    @Test
    public void testCloseVacancy() {
        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId()))
                .thenReturn(vacancy);
        VacancyDto closedVacancy = vacancyService.closeVacancy(vacancyDto);
        assertEquals(vacancyDto, closedVacancy);
        Mockito.verify(vacancyRepository, Mockito.times(1)).getReferenceById(vacancyDto.getId());
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(vacancy);
    }

//    @Test
//    public void testHireCandidate(){
//        Mockito.when(vacancyRepository.getReferenceById(vacancyDto.getId()))
//                .thenReturn(vacancy);
//        Mockito.when(teamMemberRepository.findById(1L))
//    }

}
