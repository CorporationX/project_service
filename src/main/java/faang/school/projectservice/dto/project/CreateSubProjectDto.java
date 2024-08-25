package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto {
    @Positive
    private long parentId;
    @NotBlank
    private String name;
    private String description;
    private Long ownerId;
    @NotNull
    private ProjectVisibility visibility;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
}
