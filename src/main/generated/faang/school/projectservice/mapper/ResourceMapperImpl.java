package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-02-10T05:27:03+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.9 (Amazon.com Inc.)"
)
@Component
public class ResourceMapperImpl implements ResourceMapper {

    @Override
    public ResourceDto toDto(Resource resource) {
        if ( resource == null ) {
            return null;
        }

        ResourceDto.ResourceDtoBuilder resourceDto = ResourceDto.builder();

        resourceDto.projectId( resourceProjectId( resource ) );
        resourceDto.id( resource.getId() );
        resourceDto.name( resource.getName() );
        resourceDto.key( resource.getKey() );
        resourceDto.size( resource.getSize() );
        resourceDto.createdAt( resource.getCreatedAt() );
        resourceDto.updatedAt( resource.getUpdatedAt() );

        return resourceDto.build();
    }

    private Long resourceProjectId(Resource resource) {
        if ( resource == null ) {
            return null;
        }
        Project project = resource.getProject();
        if ( project == null ) {
            return null;
        }
        Long id = project.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
