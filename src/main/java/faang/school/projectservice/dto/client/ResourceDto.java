package faang.school.projectservice.dto.client;

import faang.school.projectservice.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceDto {
    @NotNull(message = "Resource's 'id' can not be null", groups = {ValidationGroups.Update.class})
    @Positive(message = "Resource's 'id' should be greater than zero", groups = {ValidationGroups.Update.class})
    private Long id;
    @NotBlank(message = "Resource's 'name' can not be empty", groups = {ValidationGroups.Update.class})
    private String name;
    @NotBlank(message = "Resource's 'key' can't be empty", groups = {ValidationGroups.Update.class})
    private String key;
    private BigInteger size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @NotNull(message = "Project's 'id' can not be null", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private Long projectId;

}
