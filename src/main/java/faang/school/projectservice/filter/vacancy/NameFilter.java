package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterRequestDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class NameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterRequestDto filterDto) {
        var namePattern = filterDto.namePattern();
        return namePattern != null && !namePattern.isBlank();
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> source, VacancyFilterRequestDto filterDto) {
        var patternString = filterDto.namePattern();
        if (patternString == null) {
            return source;
        }

        var namePattern = Pattern.compile(patternString);

        return source.filter(vacancy -> {
            var name = vacancy.getName();
            return name != null && namePattern.matcher(name).matches();
        });
    }
}
