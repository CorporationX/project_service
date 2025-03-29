package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.model.Vacancy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.stream.Stream;

public class ReturnEmptyStreamVacancyAnswer implements Answer<Stream<Vacancy>> {

    @Override
    public Stream<Vacancy> answer(InvocationOnMock invocationOnMock) throws Throwable {
        return Stream.empty();
    }
}

