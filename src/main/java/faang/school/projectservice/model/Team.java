package faang.school.projectservice.model;

import faang.school.projectservice.model.meet.Meet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Entity
@Table(name = "team")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"meets", "teamMembers"})
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "team")
    @Column(name = "team_member_id")
    private List<TeamMember> teamMembers;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "team")
    private List<Meet> meets;

    public void addMeet(Meet meet) {
        meets.add(meet);
        meet.setTeam(this);
    }

    public void removeMeet(Meet meet) {
        meets.remove(meet);
        meet.setTeam(null);
    }
}
