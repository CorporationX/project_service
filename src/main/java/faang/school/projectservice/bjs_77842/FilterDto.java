package faang.school.projectservice.bjs_77842;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FilterDto {

    @Size(max = 255, message = "Author name should not exceed 255 symbols")
    private String author;

    @Size(max = 255, message = "Title should not exceed 255 symbol")
    private String title;

    @Size(max = 255, message = "Description should not exceed 255 symbols")
    private String description;

   }