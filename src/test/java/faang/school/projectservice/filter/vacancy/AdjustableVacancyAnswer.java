package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.model.Vacancy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class AdjustableVacancyAnswer implements Answer<Stream<Vacancy>> {
    private final Predicate<Vacancy> filter;

    // Почему-то в тестах Mockito не видит конструктор, созданный с помощью аннотации @RequiredArgsConstructor
    public AdjustableVacancyAnswer(Predicate<Vacancy> filter) {
        this.filter = filter;
    }

    @Override
    public Stream<Vacancy> answer(InvocationOnMock invocationOnMock) throws Throwable {
        Stream<Vacancy> source = invocationOnMock.getArgument(0);

        return source.filter(filter);
    }
}
