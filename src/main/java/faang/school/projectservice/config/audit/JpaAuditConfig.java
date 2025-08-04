package faang.school.projectservice.config.audit;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Profile("dev")
public class JpaAuditConfig {
}
