package faang.school.projectservice.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageFilterDto {

    private String role;
    private String status;

    public Optional<String> getRole() {
        return Optional.ofNullable(role);
    }

    public Optional<String> getStatus() {
        return Optional.ofNullable(status);
    }
}
