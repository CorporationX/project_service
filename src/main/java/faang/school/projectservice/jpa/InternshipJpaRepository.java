package faang.school.projectservice.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipJpaRepository {

    @Query(
            "INSERT INTO internship_interns (internship_id, team_member_id) " +
            "VALUES(InternshipId, TeamMemberId)"
    )
    void addInternToInternship(long InternshipId, long TeamMemberId);
}
