package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
public class CreatedRangeFilter implements MomentFilter {
    @Override
    public Boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getCreatedAtMin() != null || momentFilterDto.getCreatedAtMax() != null;
    }

    @Override
    public Stream<Moment> apply(MomentFilterDto momentFilterDto, Stream<Moment> moments) {
        LocalDateTime createdAtMax = momentFilterDto.getCreatedAtMax();
        LocalDateTime createdAtMin = momentFilterDto.getCreatedAtMin();

        if (createdAtMax == null && createdAtMin != null) {
            return moments.filter(moment -> moment.getCreatedAt().isAfter(createdAtMin));
        } else if (createdAtMax != null && createdAtMin == null) {
            return moments.filter(moment -> moment.getCreatedAt().isBefore(createdAtMax));
        }

        return moments.filter(moment ->
                moment.getCreatedAt().isAfter(createdAtMin) &&
                        moment.getCreatedAt().isBefore(createdAtMax));
    }
}
