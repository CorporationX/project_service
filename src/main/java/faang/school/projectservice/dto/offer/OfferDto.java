package faang.school.projectservice.dto.offer;

import faang.school.projectservice.model.OfferStatus;
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
    private OfferStatus status;
}
