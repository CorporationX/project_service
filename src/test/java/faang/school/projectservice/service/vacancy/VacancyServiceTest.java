package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.srvice.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private VacancyValidator vacancyValidator;
    @Mock
    private VacancyMapper vacancyMapper;
    @InjectMocks
    private VacancyService service;

    @Test
    public void deleteVacancy_Test() {
        VacancyDto vacancyDto = VacancyDto.builder().candidates(List.of(1L, 2L)).build();
        long deleterId = 9;

        service.deleteVacancy(vacancyDto, deleterId);

        Mockito.verify(candidateRepository).deleteAllByIdInBatch(vacancyDto.getCandidates());
        Mockito.verify(vacancyRepository).deleteById(vacancyDto.getId());
    }

    @Test
    public void updateVacancy_NeedEmployee_Test() {
        VacancyDto vacancyDto = VacancyDto.builder().id(1L).build();
        long updaterId = 9;

        Mockito.when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(Optional.of(Vacancy.builder().id(1L).status(VacancyStatus.OPEN).build()));
        Mockito.when(vacancyMapper.toEntity(vacancyDto)).thenReturn(Vacancy.builder().id(1L).status(VacancyStatus.CLOSED).count(2).build());

        VacancyValidationException exception = Assertions.assertThrows(VacancyValidationException.class, () -> service.updateVacancy(vacancyDto, updaterId));

        Assertions.assertEquals(exception.getMessage(), "Still need employee!");
    }

    @Test
    public void updateVacancy_Test() {
        VacancyDto vacancyDto = VacancyDto.builder().id(1L).build();
        long updaterId = 9;
        Optional<Vacancy> target = Optional.of(Vacancy.builder().id(1L).status(VacancyStatus.OPEN).updatedAt(LocalDateTime.now().minus(1, ChronoUnit.DAYS)).build());
        Vacancy source = Vacancy.builder().id(1L).status(VacancyStatus.CLOSED).count(0).build();

        Mockito.when(vacancyRepository.findById(vacancyDto.getId())).thenReturn(target);
        Mockito.when(vacancyMapper.toEntity(vacancyDto)).thenReturn(source);
        Mockito.when(vacancyRepository.save(source)).thenReturn(source);

        service.updateVacancy(vacancyDto, updaterId);

        Mockito.verify(vacancyMapper, Mockito.times(1)).toDto(source);
        Assertions.assertTrue(target.get().getUpdatedAt().isBefore(source.getUpdatedAt()));
    }
}
