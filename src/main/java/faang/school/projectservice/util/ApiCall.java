package faang.school.projectservice.util;

import java.io.IOException;

@FunctionalInterface
public interface ApiCall<T> {
    T execute() throws IOException;
}
