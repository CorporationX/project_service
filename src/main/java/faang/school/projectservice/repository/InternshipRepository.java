package faang.school.projectservice.repository;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<TeamMember> findByInternshipIdIn(List<Long> ids);
}
