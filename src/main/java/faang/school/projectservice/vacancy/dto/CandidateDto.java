package faang.school.projectservice.vacancy.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CandidateDto {
    private UUID id;
    private String name;
    private List<String> skills;
    private String experience;
}
