package faang.school.projectservice.bjs_77842;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FilterDto {

    @NotBlank(message = "Author cannot be blank")
    @Size(max = 255)
    private String author;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String description;

}