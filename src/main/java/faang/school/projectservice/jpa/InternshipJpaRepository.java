package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InternshipJpaRepository extends JpaRepository<Internship, Long> {

    Optional<Internship> findByName(String name);
}
