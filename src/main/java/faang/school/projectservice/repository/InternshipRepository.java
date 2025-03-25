package faang.school.projectservice.repository;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    @Query("SELECT i " +
            "FROM Internship i " +
            "WHERE (:status IS NULL OR i.status = :status)")
    List<Internship> findAll(@Param("status") InternshipStatus status);
}
