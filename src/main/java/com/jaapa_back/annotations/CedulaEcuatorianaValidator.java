package com.jaapa_back.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CedulaEcuatorianaValidator implements ConstraintValidator<ValidCedulaEcuatoriana, String> {

    private static final Logger log = LoggerFactory.getLogger(CedulaEcuatorianaValidator.class);

    @Override
    public boolean isValid(String cedula, ConstraintValidatorContext context) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return true; // Usar @NotBlank por separado
        }

        return validarCedulaEcuatoriana(cedula.trim());
    }

    private boolean validarCedulaEcuatoriana(String cedula) {
        try {
            // Verificar longitud
            if (cedula.length() != 10) {
                return false;
            }

            // Verificar que sean solo dígitos
            if (!cedula.matches("\\d{10}")) {
                return false;
            }

            // Verificar que los primeros 2 dígitos correspondan a una provincia válida (01-24)
            int provincia = Integer.parseInt(cedula.substring(0, 2));
            if (provincia < 1 || provincia > 24) {
                return false;
            }

            // Verificar que el tercer dígito sea menor a 6 (cédula de persona natural)
            int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
            if (tercerDigito >= 6) {
                return false;
            }

            // Algoritmo de validación del dígito verificador
            return validarDigitoVerificador(cedula);

        } catch (NumberFormatException e) {
            log.warn("Error al validar cédula: {}", e.getMessage());
            return false;
        }
    }

    private boolean validarDigitoVerificador(String cedula) {
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            int digito = Integer.parseInt(cedula.substring(i, i + 1));
            int resultado = digito * coeficientes[i];

            // Si el resultado es mayor a 9, se suma sus dígitos
            if (resultado > 9) {
                resultado = resultado - 9;
            }

            suma += resultado;
        }

        // Calcular dígito verificador
        int residuo = suma % 10;
        int digitoVerificador = residuo == 0 ? 0 : 10 - residuo;

        // Comparar con el último dígito de la cédula
        int ultimoDigito = Integer.parseInt(cedula.substring(9, 10));

        return digitoVerificador == ultimoDigito;
    }
}