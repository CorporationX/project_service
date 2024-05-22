package faang.school.projectservice.repository;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {

    List<Internship> findByStatus(InternshipStatus status);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO internship_interns (internship_id, team_member_id) VALUES (:internshipId, :teamMemberId)", nativeQuery = true)
    void addInternToInternship(long internshipId, long teamMemberId);
}
