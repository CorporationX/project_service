package faang.school.projectservice.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.filter.VacancyPositionFilter;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class VacancyPositionFilterTest {
    private VacancyPositionFilter vacancyPositionFilter;
    private List<Vacancy> vacancies;

    @BeforeEach
    public void setup(){
        vacancyPositionFilter = new VacancyPositionFilter();
        Vacancy vacancy1 = new Vacancy();
        vacancy1.setStatus(VacancyStatus.OPEN);

        Vacancy vacancy2 = new Vacancy();
        vacancy2.setStatus(VacancyStatus.POSTPONED);

        vacancies = List.of(vacancy1, vacancy2);
    }

    @Test
    public void testIsApplicable(){
        VacancyFilterDto vacancyFilterDto = new VacancyFilterDto();
        vacancyFilterDto.setStatus(VacancyStatus.OPEN);

        boolean result = vacancyPositionFilter.isApplicable(vacancyFilterDto);

        Assertions.assertTrue(result);
    }

    @Test
    public void testApply(){
        VacancyFilterDto vacancyFilterDto = new VacancyFilterDto();
        vacancyFilterDto.setStatus(VacancyStatus.OPEN);

        Stream<Vacancy> resultStream = vacancyPositionFilter.apply(vacancies.stream(), vacancyFilterDto);
        List<Vacancy> resultFilter = resultStream.toList();

        Assertions.assertEquals(1, resultFilter.size());
        Assertions.assertEquals(VacancyStatus.OPEN, resultFilter.get(0).getStatus());
    }
}
