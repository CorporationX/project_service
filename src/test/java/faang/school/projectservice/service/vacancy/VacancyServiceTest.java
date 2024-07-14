package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private VacancyValidator vacancyValidator;

    @Mock
    private List<Filter<VacancyFilterDto, Vacancy>> filter;

    @InjectMocks
    private VacancyService vacancyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createVacancy() {
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();

        when(vacancyMapper.toEntity(any(VacancyDto.class))).thenReturn(vacancy);
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
        when(vacancyMapper.toDto(any(Vacancy.class))).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.createVacancy(vacancyDto);

        assertEquals(vacancyDto, result);
        verify(vacancyValidator).validatorForCreateVacancyMethod(vacancyDto);
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    void updateVacancy() {
        Long id = 1L;
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();

        when(vacancyRepository.findById(any(Long.class))).thenReturn(Optional.of(vacancy));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);
        when(vacancyMapper.toDto(any(Vacancy.class))).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.updateVacancy(id, vacancyDto);

        assertEquals(vacancyDto, result);
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    void deleteVacancy() {
        Long vacancyId = 1L;
        Vacancy vacancy = new Vacancy();
        vacancy.setCandidates(List.of());

        when(vacancyRepository.findById(any(Long.class))).thenReturn(Optional.of(vacancy));

        vacancyService.deleteVacancy(vacancyId);

        verify(vacancyRepository).deleteById(vacancyId);
    }

    @Test
    void getVacancyById() {
        Long vacancyId = 1L;
        VacancyDto vacancyDto = new VacancyDto();
        Vacancy vacancy = new Vacancy();

        when(vacancyRepository.findById(any(Long.class))).thenReturn(Optional.of(vacancy));
        when(vacancyMapper.toDto(any(Vacancy.class))).thenReturn(vacancyDto);

        VacancyDto result = vacancyService.getVacancyById(vacancyId);

        assertEquals(vacancyDto, result);
    }

    @Test
    void getAllVacanciesByFilter() {
        VacancyFilterDto filters = new VacancyFilterDto();
        Vacancy vacancy = new Vacancy();
        VacancyDto vacancyDto = new VacancyDto();
        Filter<VacancyFilterDto, Vacancy> vacancyFilter = mock(Filter.class);
        List<Vacancy> vacancies = Collections.singletonList(vacancy);

        when(vacancyRepository.findAll()).thenReturn(vacancies);
        when(vacancyFilter.isApplicable(filters)).thenReturn(true);
        when(vacancyFilter.apply(any(Stream.class), eq(filters))).thenReturn(vacancies.stream());
        when(filter.stream()).thenReturn(Stream.of(vacancyFilter));
        when(vacancyMapper.toDto(any(Vacancy.class))).thenReturn(vacancyDto);

        List<VacancyDto> result = vacancyService.getAllVacanciesByFilter(filters);

        assertEquals(1, result.size());
        assertEquals(vacancyDto, result.get(0));
    }
}
