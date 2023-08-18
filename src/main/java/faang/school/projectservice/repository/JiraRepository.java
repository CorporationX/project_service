package faang.school.projectservice.repository;

import faang.school.projectservice.model.Jira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JiraRepository extends JpaRepository<Jira, Long> {

    @Query(nativeQuery = true, value = """
            SELECT j.* FROM jira j WHERE j.project_id = :projectId""")
    Optional<Jira> findByProjectId(long projectId);
}
