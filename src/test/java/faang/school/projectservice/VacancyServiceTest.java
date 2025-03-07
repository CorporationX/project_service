package faang.school.projectservice;

import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Spy
    private VacancyMapper vacancyMapper = new VacancyMapperImpl();
    @InjectMocks
    private VacancyService vacancyService;



    @Test
    void createVacancyTest() {
        Vacancy vacancy = new Vacancy();
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        vacancyService.createVacancy(vacancy);
        verify(vacancyRepository, times(1)).save(vacancy);
        }

    @Test
    public void testVacancyClosedWithNullCandidates () {
        Vacancy vacancy = new Vacancy();
        vacancyInitializer(vacancy);
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setCandidates(null);
        Assertions.assertThrows(IllegalStateException.class, () ->vacancyService.updateVacancy(vacancy));
    }
    @Test
    public void testVacancyClosedWithLessCandidates () {
        Vacancy vacancy = new Vacancy();
        vacancyInitializer(vacancy);
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setCandidates(Collections.singletonList(new Candidate()));
        vacancy.setCount(3);
        Assertions.assertThrows(IllegalStateException.class, () ->vacancyService.updateVacancy(vacancy));
    }

   @Test
   public void updateVacancyTest () {
       Vacancy vacancy = new Vacancy();
       vacancyInitializer(vacancy);
       when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
       vacancyService.updateVacancy(vacancy);
            verify(vacancyRepository, times(1)).save(vacancy);
   }
   @Test
   public void testDeleteVacancy () {
        VacancyUpdateDto vacancyDto = new VacancyUpdateDto();
        dtoInitializer(vacancyDto);
        Vacancy vacancy = new Vacancy();
        vacancyMapper.update(vacancy,vacancyDto);
        List <Candidate> candidates = vacancy.getCandidates();
        candidates.stream().allMatch(candidate -> candidate.getCandidateStatus() == null);
        when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(vacancy));
       vacancyService.deleteVacancy(vacancyDto.getId());
       verify(vacancyRepository, times(1)).deleteById(vacancy.getId());
    }

    public void dtoInitializer (VacancyUpdateDto vacancyDto) {
        vacancyDto.setRoleId(0L);
        vacancyDto.setProjectId(1L);
        vacancyDto.setPositionId(3);
        vacancyDto.setId(1L);
        vacancyDto.setName("Java Developer");
        vacancyDto.setCount(1);
        vacancyDto.setCandidatesIds(Collections.singletonList(3L));
        vacancyDto.setStatusId(1);
        vacancyDto.setDescription("Java Developer");
        vacancyDto.setSalary(1000.0);
        vacancyDto.setCoverImageKey("Java Developer");
        vacancyDto.setRequiredSkillIds(Collections.singletonList(1L));
    }
    public void vacancyInitializer (Vacancy vacancy) {
        vacancy.setProject(new Project());
        vacancy.setPosition(TeamRole.DEVELOPER);
        vacancy.setId(5L);
        vacancy.setName("Java Developer");
        vacancy.setCount(1);
        vacancy.setCandidates(Collections.singletonList(new Candidate()));
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setDescription("Java Developer");
        vacancy.setSalary(1000.0);
        vacancy.setCoverImageKey("Java Developer");
        vacancy.setRequiredSkillIds(Collections.singletonList(1L));
    }

}
