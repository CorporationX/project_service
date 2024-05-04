package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;

import java.util.stream.Stream;

public class VacancyFiltersSetup {
    VacancyFilterDto filter1;
    VacancyFilterDto filter2;
    VacancyFilterDto filter3;
    Vacancy vacancy1;
    Vacancy vacancy2;
    Vacancy vacancy3;
    Stream<Vacancy> vacancyStream;

    @BeforeEach
    void setup() {
        String name = "abc";
        TeamRole position = TeamRole.DEVELOPER;
        vacancy1 = Vacancy.builder()
                .name(name)
                .position(TeamRole.INTERN)
                .build();
        vacancy2 = Vacancy.builder()
                .name("")
                .position(position)
                .build();
        vacancy3 = Vacancy.builder()
                .name(name)
                .position(position)
                .build();
        filter1 = VacancyFilterDto.builder()
                .namePattern(name)
                .build();
        filter2 = VacancyFilterDto.builder()
                .positionPattern(position)
                .build();
        filter3 = VacancyFilterDto.builder()
                .namePattern(name)
                .positionPattern(position)
                .build();
        vacancyStream = Stream.of(vacancy1, vacancy2, vacancy3);
    }
}
