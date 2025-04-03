package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import jakarta.validation.constraints.NotNull;

import java.util.stream.Stream;

/**
 * Интерфейс для фильтрации донатов.
 * Определяет метод для проверки применимости фильтра и метод его применения к потоку донатов.
 */
public interface DonationFilter {

    /**
     * Проверяет, применим ли данный фильтр к указанному DTO фильтра.
     *
     * @param filter DTO фильтра.
     * @return true, если фильтр применим, иначе false.
     */
    boolean isApplicable(@NotNull DonationFilterDto filter);

    /**
     * Применение фильтра к потоку донатов
     *
     * @param donations Поток донатов, которые необходимо отфильтровать
     * @param filter DTO фильтра с данными для сортировки
     * @return Отфильтрованный поток донатов
     */
    Stream<Donation> apply(Stream<Donation> donations, DonationFilterDto filter);
}
