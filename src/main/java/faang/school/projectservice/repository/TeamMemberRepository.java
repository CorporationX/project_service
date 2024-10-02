package faang.school.projectservice.repository;

import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

}
