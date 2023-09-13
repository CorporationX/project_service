package faang.school.projectservice.dto.company;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CampaignDto {
    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 128, message = "Name must be less than 128 characters")
    private String title;

    @NotBlank
    @Size(max = 4096, message = "Name must be less than 4096 characters")
    private String description;

    @NotNull
    private Long projectId;

    private Currency currency;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    private Long createdBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NotNull
    private Long updatedBy;
}
