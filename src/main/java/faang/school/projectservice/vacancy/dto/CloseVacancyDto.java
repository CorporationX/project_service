package faang.school.projectservice.vacancy.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CloseVacancyDto {
    private List<UUID> selectedCandidateIds;
}
