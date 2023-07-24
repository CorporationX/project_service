package faang.school.projectservice.model;

import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private Team team;

    @ManyToMany(mappedBy = "executors")
    private List<Stage> stages;

    public void addRole(TeamRole teamRole) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(teamRole);
    }

    public void finishInternship() {
        TeamRole role = roles.stream().filter(r ->
                        r.equals(TeamRole.INTERN_ANALYST) ||
                                r.equals(TeamRole.INTERN_DESIGNER) ||
                                r.equals(TeamRole.INTERN_DEVELOPER) ||
                                r.equals(TeamRole.INTERN_MANAGER) ||
                                r.equals(TeamRole.INTERN_TESTER)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Team member with id " + id + " doesn't has intern role"));

        switch (role) {
            case INTERN_ANALYST -> {
                roles.remove(TeamRole.INTERN_ANALYST);
                roles.add(TeamRole.ANALYST);
            }
            case INTERN_DESIGNER -> {
                roles.remove(TeamRole.INTERN_DESIGNER);
                roles.add(TeamRole.DESIGNER);
            }
            case INTERN_DEVELOPER -> {
                roles.remove(TeamRole.INTERN_DEVELOPER);
                roles.add(TeamRole.DEVELOPER);
            }
            case INTERN_MANAGER -> {
                roles.remove(TeamRole.INTERN_MANAGER);
                roles.add(TeamRole.MANAGER);
            }
            case INTERN_TESTER -> {
                roles.remove(TeamRole.INTERN_TESTER);
                roles.add(TeamRole.TESTER);
            }
        }
    }
}
