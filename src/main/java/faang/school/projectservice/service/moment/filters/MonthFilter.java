package faang.school.projectservice.service.moment.filters;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Data
@Builder
@AllArgsConstructor
@Component
public class MonthFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return filters.getMonth() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments
                .filter(moment -> moment.getDate().getMonth().getValue() == filters.getMonth());
    }
}

