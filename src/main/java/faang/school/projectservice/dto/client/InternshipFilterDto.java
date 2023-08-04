package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor

public class InternshipFilterDto {
    private InternshipStatus status;
    private String name;
}
