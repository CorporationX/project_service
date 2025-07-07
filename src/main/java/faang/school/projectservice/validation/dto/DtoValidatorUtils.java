package faang.school.projectservice.validation.dto;

import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Objects;

public class DtoValidatorUtils {

    public static boolean isAllFieldsNull(Object dto) {
        if (dto == null) return true;

        BeanWrapperImpl wrapper = new BeanWrapperImpl(dto);
        PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();

        return Arrays.stream(propertyDescriptors)
                .map(PropertyDescriptor::getName)
                .filter(name -> !"class".equals(name)) // игнорируем служебное поле
                .map(wrapper::getPropertyValue)
                .allMatch(Objects::isNull);
    }
}
