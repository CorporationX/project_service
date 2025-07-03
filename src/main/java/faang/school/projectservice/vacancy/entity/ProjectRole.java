package faang.school.projectservice.vacancy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "project_role")
@Data
@AllArgsConstructor
public class ProjectRole {
    @Id
    private UUID id;

    private UUID projectId;
    private UUID userId;
    private String role;
}
