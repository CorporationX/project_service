package faang.school.projectservice.service.VacancyFilters;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VacancyFilterTest {
    static Stream<VacancyFilter> argsProvider1() {
        return Stream.of(
                new VacancyNameFilter(),
                new VacancyDescriptionFilter(),
                new VacancyStatusFilter(),
                new VacancyRequiredSkillFilter()
        );
    }

    static Stream<Arguments> argsProvider2() {
        return Stream.of(
                Arguments.of(new VacancyNameFilter(), new VacancyFilterDto("1", null, null, null)),
                Arguments.of(new VacancyDescriptionFilter(), new VacancyFilterDto(null, "1", null, null)),
                Arguments.of(new VacancyStatusFilter(), new VacancyFilterDto(null, null, VacancyStatus.OPEN, null)),
                Arguments.of(new VacancyRequiredSkillFilter(), new VacancyFilterDto(null, null, null, 1L))
        );
    }

    @ParameterizedTest
    @MethodSource("argsProvider1")
    public void testIsApplicable(VacancyFilter vacancyFilter) {
        VacancyFilterDto filter = new VacancyFilterDto("1", "1", VacancyStatus.OPEN, 1L);

        boolean result1 = vacancyFilter.isApplicable(filter);
        boolean result2 = vacancyFilter.isApplicable(new VacancyFilterDto());

        assertTrue(result1);
        assertFalse(result2);
    }

    @ParameterizedTest
    @MethodSource("argsProvider2")
    public void testApply(VacancyFilter vacancyFilter, VacancyFilterDto filter) {
        Vacancy vacancy = Vacancy
                .builder()
                .name("1")
                .description("Ya111V111Zalozhnikah111Pomogite")
                .requiredSkillIds(List.of(1L))
                .status(VacancyStatus.OPEN)
                .build();

        List<Vacancy> result = vacancyFilter.apply(Stream.of(vacancy), filter);

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(vacancy, result.get(0))
        );
    }
}