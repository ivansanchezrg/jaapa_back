---------------------------------------CONFIGURACIÓN DE USUARIOS Y ROLES -------------------------------------
-- Primero borra los datos existentes si hay
DELETE FROM usuarios_sistema_roles;
DELETE FROM jaapa_usuarios_sistema;
DELETE FROM jaapa_roles;

-- Reinicia las secuencias
ALTER SEQUENCE jaapa_usuarios_sistema_usu_sis_id_seq RESTART WITH 1;
ALTER SEQUENCE jaapa_roles_rol_id_seq RESTART WITH 1;

-- Crear roles básicos
INSERT INTO jaapa_roles (rol_nombre, rol_descripcion)
VALUES
('SUPER_ADMIN', 'Control total del sistema'),
('ADMIN', 'Gestión de operadores y sistema'),
('OPERADOR', 'Acceso limitado solo al módulo de empleado.');

-- Crear super_admin inicial con contraseña Admin@JAAPA2024!
INSERT INTO jaapa_usuarios_sistema (usu_sis_username, usu_sis_password, enabled)
VALUES ('super_admin', '$2a$10$0JTphi.zAMWolPSjYu5Gr.9./xBp3sRMSjqQ8lEBY4LHsTuZ4t80S', true);

-- Asignar rol super_admin
INSERT INTO usuarios_sistema_roles (usuario_id, role_id)
SELECT u.usu_sis_id, r.rol_id
FROM jaapa_usuarios_sistema u, jaapa_roles r
WHERE u.usu_sis_username = 'super_admin' AND r.rol_nombre = 'SUPER_ADMIN';