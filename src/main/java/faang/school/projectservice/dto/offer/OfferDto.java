package faang.school.projectservice.dto.offer;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferDto {
    private Long id;
    private Long vacancyId;
    private Long candidateId;
    private Long createdBy;
    private Long teamId;
    private InternshipStatus status;
}
