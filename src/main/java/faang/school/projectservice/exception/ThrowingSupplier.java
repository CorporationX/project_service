package faang.school.projectservice.exception;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
}