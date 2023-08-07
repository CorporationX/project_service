package faang.school.projectservice.filters.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootContextLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FilterBySkillTest {
    private static final int COUNT = 3;
    private List<Long> skills1;
    private List<Long> skills2;
    private List<Long> skills3;
    private List<Vacancy> vacancyList;
    private VacancyFilterDto filterDto;

    private final FilterBySkill filter = new FilterBySkill();

    @BeforeEach
    void setUp() {
        skills1 = List.of(1L, 2L, 3L);
        skills2 = List.of(3L, 4L, 5L, 2L);
        skills3 = List.of(6L, 7L, 8L);
    }

    @Test
    void isApplicable() {
    }

    @Test
    void testApply() {
        List<Vacancy> expectedVacancies = getExpectedVacanciesAfterFilter();
        vacancyList = getVacancyList();
        Stream<Vacancy> streamVacancies = vacancyList.stream();
        List<Long> needSkill = List.of(2L, 3L);
        filterDto = VacancyFilterDto.builder()
                .skillsPattern(needSkill).build();

        List<Vacancy> result = filter.apply(streamVacancies, filterDto).toList();

        assertEquals(expectedVacancies, result);
    }

    private List<Vacancy> getVacancyList() {
        List<List<Long>> skills = List.of(
                skills1,
                skills2,
                skills3
        );

        List<Vacancy> vacancies = new ArrayList<>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            Vacancy vacancy = Vacancy.builder()
                    .id(i + 1L)
                    .requiredSkillIds(skills.get(i))
                    .build();
            vacancies.add(vacancy);
        }
        return vacancies;
    }

    private List<Vacancy> getExpectedVacanciesAfterFilter() {
        Vacancy vacancy1 = Vacancy.builder()
                .id(1L)
                .requiredSkillIds(skills1)
                .build();
        Vacancy vacancy2 = Vacancy.builder()
                .id(2L)
                .requiredSkillIds(skills2)
                .build();

        return List.of(vacancy1,
                vacancy2);
    }

    private List<List<Long>> getListSkills() {
        List<List<Long>> listsSkills = new ArrayList<>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            List<Long> skills = new ArrayList<>();
        }
        return listsSkills;
    }
}