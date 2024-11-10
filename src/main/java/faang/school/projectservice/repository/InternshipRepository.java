package faang.school.projectservice.repository;

import faang.school.projectservice.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {

    @Modifying
    @Query("DELETE FROM Internship i WHERE i.id = :internshipId AND :internId MEMBER OF i.interns")
    void removeInternFromProject(@Param("internId") Long internId, @Param("internshipId") Long internshipId);
}
