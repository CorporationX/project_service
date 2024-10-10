package faang.school.projectservice.repository;

import faang.school.projectservice.model.entity.GoogleCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleCredentialRepository extends JpaRepository<GoogleCredentialEntity, Long> {
}

