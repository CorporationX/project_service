package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentMonthFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentDto momentDto) {
        return momentDto.getFilterMonth() != null &&
                momentDto.getFilterMonth() >= 1 &&
                momentDto.getFilterMonth() <= 12;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentDto momentDto) {
        Integer monthToFilter = momentDto.getFilterMonth();

        return moments
                .filter(moment -> moment.getDate() != null &&
                        moment.getDate().getMonthValue() == monthToFilter);
    }
}
