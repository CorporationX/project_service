package faang.school.projectservice.filter;

@FunctionalInterface
public interface Filter<F> {
    boolean matches(F filter);
}
