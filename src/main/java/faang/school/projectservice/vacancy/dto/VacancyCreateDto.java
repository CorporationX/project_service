package faang.school.projectservice.vacancy.dto;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

import java.util.UUID;

@Data
public class VacancyCreateDto {
    private UUID projectId;
    private String title;
    private TeamRole position;
    private int slots;
}

