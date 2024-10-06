package faang.school.projectservice.repository;

import faang.school.projectservice.model.GoogleAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<GoogleAuthToken, Long> {
    GoogleAuthToken findFirstByOrderByCreatedAtDesc();
}
