package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public record DataRangeFilter(@NotBlank String month) implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getDate() != null;

    }

    @Override
    public void apply(List<Moment> moments, MomentFilterDto filter) {
        moments.removeIf(moment -> !moment.getDate().toLocalDate().equals(filterLocalData));
    }
}

