package com.jaapa_back.service;

import com.jaapa_back.exception.custom.ValorActualIncorrectoException;
import org.springframework.stereotype.Service;

@Service
public class VerificarCedulaService {

    public void verificarCedula(String cedula) throws ValorActualIncorrectoException {
        this.verificarCedulaNoNula(cedula);
        this.verificarCedulaSoloNumeros(cedula);
        verificarCedulaLongitudCorrecta(cedula);

        int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
        if (tercerDigito >= 6) {
            throw new ValorActualIncorrectoException("El tercer dígito de la cédula debe ser menor a 6");
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia <= 0 || provincia >= 25) {
            throw new ValorActualIncorrectoException("El código de provincia no es válido");
        }

        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int verificador = Integer.parseInt(cedula.substring(9,10));
        int suma = 0;

        for (int i = 0; i < coeficientes.length; i++) {
            int valor = Integer.parseInt(cedula.substring(i, i + 1)) * coeficientes[i];
            suma += valor > 9 ? valor - 9 : valor;
        }

        int modulo = suma % 10;
        int digitoVerificador = modulo == 0 ? 0 : 10 - modulo;

        if (digitoVerificador != verificador) {
            throw new ValorActualIncorrectoException("La cédula no es valida.");
        }
    }

    /**
     * Verifica si la cédula es nula.
     *
     * @param cedula La cédula a verificar.
     * @throws ValorActualIncorrectoException Si la cédula es nula.
     */
    public void verificarCedulaNoNula(String cedula) throws ValorActualIncorrectoException {
        if (cedula == null) {
            throw new ValorActualIncorrectoException("La cédula no puede ser nula");
        }
    }

    /**
     * Verifica si la cédula contiene solo números.
     *
     * @param cedula La cédula a verificar.
     * @throws ValorActualIncorrectoException Si la cédula contiene caracteres no numéricos.
     */
    public void verificarCedulaSoloNumeros(String cedula) throws ValorActualIncorrectoException {
        verificarCedulaNoNula(cedula);
        if (!cedula.matches("\\d+")) {
            throw new ValorActualIncorrectoException("Por favor, escribe tu cédula sin espacios ni letras");
        }
    }

    /**
     * Verifica si la cédula tiene la longitud correcta.
     *
     * @param cedula La cédula a verificar.
     * @throws ValorActualIncorrectoException Si la cédula no tiene 10 dígitos.
     */
    public void verificarCedulaLongitudCorrecta(String cedula) throws ValorActualIncorrectoException {
        verificarCedulaNoNula(cedula);
        if (cedula.length() != 10) {
            throw new ValorActualIncorrectoException("La cédula debe tener exactamente 10 dígitos");
        }
    }
}
