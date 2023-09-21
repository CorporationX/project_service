package faang.school.projectservice.jpa;

import faang.school.projectservice.model.jira.JiraProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JiraProjectRepository extends JpaRepository<JiraProject, Long> {
}
