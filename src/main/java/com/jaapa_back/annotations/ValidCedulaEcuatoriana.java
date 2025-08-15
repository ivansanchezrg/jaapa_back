package com.jaapa_back.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CedulaEcuatorianaValidator.class)
@Documented
public @interface ValidCedulaEcuatoriana {
    String message() default "Cédula ecuatoriana inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}