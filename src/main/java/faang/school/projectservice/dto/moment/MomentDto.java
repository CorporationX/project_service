package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MomentDto {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 90)
    private String name;

    @NotNull
    @Past
    private LocalDateTime date;

    @NotEmpty
    private List<Long> projectsIDs;

    @NotNull
    private List<Long> userIDs;

    public synchronized void setUserIDs(List<Long> userIDs) {
        this.userIDs = userIDs;
    }

    public synchronized List<Long> getUserIDs() {
        return userIDs;
    }
}
