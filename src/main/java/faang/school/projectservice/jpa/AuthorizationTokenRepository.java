package faang.school.projectservice.jpa;

import faang.school.projectservice.model.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationTokenRepository extends JpaRepository<AuthEntity, Long> {
}
