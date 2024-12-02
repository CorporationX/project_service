package faang.school.projectservice.model;

import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team_member")
@Builder
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ElementCollection(targetClass = TeamRole.class)
    @CollectionTable(name = "team_member_roles",
            joinColumns = @JoinColumn(name = "team_member_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private List<TeamRole> roles;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    @ToString.Exclude
    private Team team;

    @ManyToMany(mappedBy = "executors")
    private List<Stage> stages;

    public boolean isCurator() {
        return roles.contains(TeamRole.OWNER) || roles.contains(TeamRole.MANAGER);
    }

    public boolean hasRole(TeamRole role) {
        return roles.contains(role);
    }

    public boolean isSameMember(Long teamMemberId) {
        return this.id.equals(teamMemberId);
    }

    public boolean isManager(){
        return roles.contains(TeamRole.MANAGER);
    }
}
