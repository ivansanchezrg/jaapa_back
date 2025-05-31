package com.jaapa_back.security.constants;

/**
 * Constantes de seguridad para el sistema.
 * Contiene expresiones de autorización predefinidas.
 */

public final class SecurityConstants {
    // Expresiones de autorización predefinidas
    public static final String HAS_SUPER_ADMIN = "hasRole('SUPER_ADMIN')";
    public static final String HAS_SUPER_ADMIN_OR_ADMIN = "hasAnyRole('SUPER_ADMIN', 'ADMIN')";
    public static final String HAS_OPERADOR = "hasAnyRole('OPERADOR')";

    private SecurityConstants() {
        // Constructor privado para evitar instanciación
    }
}
