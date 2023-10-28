package faang.school.projectservice.repository;

import faang.school.projectservice.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query(nativeQuery = true, value = """
            INSERT INTO team (project_id)
            VALUES (?) returning id
            """)
    Long create(long projectId);
}
