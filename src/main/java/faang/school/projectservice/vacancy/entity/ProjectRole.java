package faang.school.projectservice.vacancy.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class ProjectRole {
    @Id
    private UUID id;

    private UUID projectId;
    private UUID userId;
    private String role;
}
