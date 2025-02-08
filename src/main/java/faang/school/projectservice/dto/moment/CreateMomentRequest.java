package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateMomentRequest(@NotBlank(message = "Название момента не может быть пустым")
                                  String name,
                                  @NotNull(message = "Момент должен относиться хотя бы к одному проекту")
                                  List<Long> projectIds) {
}
