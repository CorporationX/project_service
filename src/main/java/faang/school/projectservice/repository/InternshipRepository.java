package faang.school.projectservice.repository;

import faang.school.projectservice.model.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {

    List<Internship> findByProjectId(Long projectId);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Internship i " +
            "WHERE i.project.id = :projectId " +
            "AND i.mentor.id = :mentorId " +
            "AND i.status IS NOT NULL " +
            "AND i.status <> faang.school.projectservice.model.enums.InternshipStatus.COMPLETED")
    boolean existsByProjectIdAndMentorIdAndStatusNotCompleted(Long projectId, Long mentorId);
}
