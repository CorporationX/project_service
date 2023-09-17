package faang.school.projectservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "jira")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Jira {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 1024, nullable = false)
    private String username;

    @Column(name = "project_key", length = 16, nullable = false)
    private String projectKey;

    @Column(name = "project_url", length = 1024, nullable = false)
    private String projectUrl;

    @OneToOne
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;
}
