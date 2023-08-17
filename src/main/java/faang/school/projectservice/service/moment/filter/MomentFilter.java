package faang.school.projectservice.service.moment.filter;


import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;
// для каждого отдельного фильтра в Dto фильтра мы напишем отдельный класс реализацию интерфейса MomentFilter
public interface MomentFilter {
    // Проверяет нужно ли определенный фильтр использовать
    boolean isApplicable(MomentFilterDto momentFilterDto);
    // применяем фильтр
    // передаем стрим моментов и  набор фильтров
    Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters);
}
