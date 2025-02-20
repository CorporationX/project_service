package faang.school.projectservice.vacancy.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddCandidatesDto {
    private List<CandidateDto> candidates;
}
