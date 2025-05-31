package com.jaapa_back.annotations;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.util.ReflectionUtils;

public class UpperCaseListener {
    @PrePersist
    @PreUpdate
    public void toUpperCase(Object entity) {
        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            if (field.isAnnotationPresent(ToUpperCase.class)) {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value instanceof String) {
                    field.set(entity, ((String) value).toUpperCase());
                }
            }
        });
    }
}
