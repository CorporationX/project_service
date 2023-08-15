package faang.school.projectservice.dto.offer;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class OfferDto {
    private Long id;
    @NonNull
    private Long vacancyId;
    @NonNull
    private Long candidateId;
    @NonNull
    private Long createdBy;
    @NonNull
    private Long teamId;
    private InternshipStatus status;
}
