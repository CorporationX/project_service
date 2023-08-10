package faang.school.projectservice.repository;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {

}
