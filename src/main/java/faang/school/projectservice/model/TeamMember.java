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

    public void addRole(TeamRole teamRole){
        if (roles == null){
            roles = new ArrayList<>();
        }
        roles.add(teamRole);
    }

    public void finishInternship() {
        TeamRole role = roles.stream()
                .filter(r ->
                        r.equals(TeamRole.INTERNANALYST) ||
                                r.equals(TeamRole.INTERNDESIGNER) ||
                                r.equals(TeamRole.INTERNDEVELOPER) ||
                                r.equals(TeamRole.INTERNMANAGER) ||
                                r.equals(TeamRole.INTERNTESTER)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Team member doesn't has intern role"));

        switch (role) {
            case INTERNANALYST -> {
                roles.remove(TeamRole.INTERNANALYST);
                roles.add(TeamRole.ANALYST);
            }
            case INTERNDESIGNER -> {
                roles.remove(TeamRole.INTERNDESIGNER);
                roles.add(TeamRole.DESIGNER);
            }
            case INTERNDEVELOPER -> {
                roles.remove(TeamRole.INTERNDEVELOPER);
                roles.add(TeamRole.DEVELOPER);
            }
            case INTERNMANAGER -> {
                roles.remove(TeamRole.INTERNMANAGER);
                roles.add(TeamRole.MANAGER);
            }
            case INTERNTESTER -> {
                roles.remove(TeamRole.INTERNTESTER);
                roles.add(TeamRole.TESTER);
            }
        }
    }
}
