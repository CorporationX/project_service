package faang.school.projectservice.repository;

import faang.school.projectservice.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
            "AND i.status <> faang.school.projectservice.model.InternshipStatus.COMPLETED")
    boolean existsByProjectIdAndMentorIdAndStatusNotCompleted(@Param("projectId") Long projectId,
                                                              @Param("mentorId") Long mentorId);
}
