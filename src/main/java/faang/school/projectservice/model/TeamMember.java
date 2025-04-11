package faang.school.projectservice.model;

import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team_member")
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @ElementCollection(targetClass = TeamRole.class)
    @CollectionTable(name = "team_member_roles", joinColumns = @JoinColumn(name = "team_member_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<TeamRole> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;

    @ManyToMany(mappedBy = "executors")
    private List<Stage> stages;

    @ManyToMany(mappedBy = "memberRoles.keySet")
    @Builder.Default
    private Set<Project> projects = new HashSet<>();

    public boolean hasRoleInProject(Project project, TeamRole role) {
        return project.getMemberRoles().getOrDefault(this, Collections.emptySet()).contains(role);
    }
}
