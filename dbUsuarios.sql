CREATE DATABASE microservicioUsuarios;
USE microservicioUsuarios;
CREATE TABLE usuarios(
	id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    cedula VARCHAR(15),
    nombre_completo VARCHAR(120),
    celular VARCHAR(15),
    correo VARCHAR(255),
    contraseña VARCHAR(255),
    rol ENUM('ADMINISTRADOR','COLABORADOR'),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
SELECT * FROM usuarios;

