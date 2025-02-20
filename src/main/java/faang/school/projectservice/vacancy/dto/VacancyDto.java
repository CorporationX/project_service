package faang.school.projectservice.vacancy.dto;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import lombok.Data;

import java.util.List;

@Data
public class VacancyDto {
    private Long id;
    private String name;
    private String position;
    private int capacity;
    private List<CandidateDto> candidates;
    private String status;
    private Long projectId;
}