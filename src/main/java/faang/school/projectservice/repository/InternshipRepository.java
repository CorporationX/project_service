package faang.school.projectservice.repository;

import faang.school.projectservice.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {
    @Query(nativeQuery = true, value = """
            select i.* from internship i
            where i.project_id = :projectId
            """)
    List<Internship> findAllByProjectId(long projectId);
}
