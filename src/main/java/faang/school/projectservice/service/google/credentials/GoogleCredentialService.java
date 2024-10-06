package faang.school.projectservice.service.google.credentials;

import faang.school.projectservice.repository.GoogleCredentialRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GoogleCredentialService {
    private final static Long GOOGLE_CREDENTIALS_ID = 1L;

    private final GoogleCredentialRepository repository;

    public String getCredentialsJson() {
        return repository.findById(GOOGLE_CREDENTIALS_ID)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Google credentials not found for id = %d", GOOGLE_CREDENTIALS_ID)))
                .getCredentialsJson();
    }
}