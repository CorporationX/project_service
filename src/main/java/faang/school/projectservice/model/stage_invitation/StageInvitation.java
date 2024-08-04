package faang.school.projectservice.model.stage_invitation;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stage_invitation")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StageInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private StageInvitationStatus status;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "stage_id")
    private Stage stage;

    @OneToOne
    @JoinColumn(name = "author")
    private TeamMember author;

    @OneToOne
    @JoinColumn(name = "invited")
    private TeamMember invited;

    public StageInvitation(TeamRole teamRole, TeamMember invited, Stage stage) {
        this.description = "This is an automatic generated message\n" +
                "You got this invite because there is deficit of " +
                teamRole.toString() +
                " on the stage with id " +
                stage.getStageId();
        this.invited = invited;
        this.stage = stage;
    }
}
