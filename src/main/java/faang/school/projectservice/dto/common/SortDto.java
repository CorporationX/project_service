package faang.school.projectservice.dto.common;

import lombok.Value;
import org.springframework.data.domain.Sort;

@Value
public class SortDto {
    String property;
    Sort.Direction direction;

    public static SortDto from(Sort sort) {
        return sort.stream()
                .findFirst()
                .map(order -> new SortDto(order.getProperty(), order.getDirection()))
                .orElse(null);
    }
}