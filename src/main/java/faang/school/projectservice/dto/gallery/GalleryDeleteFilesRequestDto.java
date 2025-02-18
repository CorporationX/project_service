package faang.school.projectservice.dto.gallery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryDeleteFilesRequestDto {
    List<String> keys;
}
