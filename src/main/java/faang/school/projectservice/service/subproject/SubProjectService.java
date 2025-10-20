package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;

import java.util.List;

public interface SubProjectService {
    SubProjectDto create(long creatorId, CreateSubProjectDto createSubProjectDto);

    SubProjectDto update(long requesterId, UpdateSubProjectDto updateSubProjectDto);

    boolean complete(long subprojectId);

    SubProjectDto getById(long subprojectId);

    List<SubProjectDto> getAllByParentProject(long parentProjectId);

    boolean delete(long subprojectId);



    //OwnerId нужно подставлять из UserContext.
    //И проверяй: подпроект нельзя создавать от чужого проекта.
    //Нельзя создать публичный подпроект для приватного родительского проекта.
    //Нельзя закрыть проект, если у него есть все ещё открытые подпроекты. Нужно сначала закрывать все подпроекты, и только потом родительский проект
    //Проверить, и если у проекта закрылись все его подпроекты, тогда получаем Moment, а участники проекта становятся участниками момента. Получается, что у проекта есть момент “Выполнены все подпроекты”.
    //Если подпроект становится приватным, то и все его подпроекты должны стать приватными.
    //Не забудьте проверить видимость проекта. Может быть такое, что публичный проект имеет секретный подпроект, тогда его нельзя показывать другим участникам.
    //И не забудь дописать все в Controller и переделать его в RestController.

}
