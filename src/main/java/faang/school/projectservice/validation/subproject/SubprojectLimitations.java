package faang.school.projectservice.validation.subproject;

import lombok.Getter;

@Getter
public enum SubprojectLimitations {

    SUBPROJECT_DESCRIPTION_CANNOT_BE_NULL("Описание проекта не может быть нулем"),
    SUBPROJECT_DESCRIPTION_CANNOT_BE_BLANC("описание проекта не может быть пустым"),
    SUBPROJECT_NAME_CANNOT_BE_NULL("Название проекта не может быть нулем"),
    SUBPROJECT_NAME_CANNOT_BE_BLANC("Название проекта не может быть пустым"),
    PARENT_PROJECT_ID_CANNOT_BE_NULL("Id родительского проекта не может быть пустым"),
    SUBPROJECT_VISIBILITY_CANNOT_BE_NULL("Не заполнена видимость для передаваемого подпроекта"),
    SUBPROJECT_STATUS_CANNOT_BE_NULL("Не заполнен статус для передаваемого подпроекта"),
    PROJECT_DOES_NOT_EXIST_BY_ID("Проекта по указанному id нет");
    private final String message;

    SubprojectLimitations(String message) {
        this.message = message;
    }
}