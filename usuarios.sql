CREATE DATABASE dbtatsoft;
USE dbtatsoft;

-- -------------- microservicio usuarios
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
select * from usuarios;


SELECT * FROM usuarios;

INSERT INTO usuarios(id_usuario,cedula,nombre_completo,celular,correo,contraseña,rol) VALUES(3,'12345', 'Cammila uribe', '1234512345', 'camila@gmail.com', 'pass12345','administrador');

desc usuarios;


drop table if exists zonas_de_trabajo;
drop table if exists clientes;
drop table if exists productos;
-- ---------------Microservice areas/clientes ------------------------------------------
create table zonas_de_trabajo(
id_zona_de_trabajo int auto_increment primary key,
nombre_zona_trabajo varchar(45),
descripcion varchar(255)
);

INSERT INTO zonas_de_trabajo (nombre_zona_trabajo, descripcion) 
VALUES 
('Zona Norte', 'Área correspondiente al norte de la ciudad, incluye barrios residenciales y comerciales.');

INSERT INTO zonas_de_trabajo (nombre_zona_trabajo, descripcion) 
VALUES 
('Zona Sur', 'Región del sur de la ciudad, principalmente industrial y de almacenes.');

INSERT INTO zonas_de_trabajo (nombre_zona_trabajo, descripcion) 
VALUES 
('Zona Centro', 'Zona céntrica con alta densidad comercial y empresarial.');

INSERT INTO zonas_de_trabajo (nombre_zona_trabajo, descripcion) 
VALUES 
('Zona Rural', 'Áreas rurales fuera del casco urbano, incluye veredas y pueblos cercanos.');


create table clientes(
id_cliente int auto_increment primary key,
cedula varchar(15) not null ,
nombre_completo_cliente varchar(200) not null,
direccion varchar(255) not null, 
telefono varchar(15) not null,
rut_nit varchar(30) null,
razon_social varchar(120) null,
fecha_registro DATE NOT NULL DEFAULT (CURRENT_DATE),
estado enum('Activo', 'Inactivo') not null default 'Activo',
id_zona_de_trabajo int,
foreign key (id_zona_de_trabajo) references zonas_de_trabajo(id_zona_de_trabajo)
on delete set null on update cascade
);
SELECT razon_social, nombre_completo_cliente, direccion, telefono FROM clientes WHERE id_cliente = 1;
INSERT INTO clientes 
(cedula, nombre_completo_cliente, direccion, telefono, rut_nit, razon_social, fecha_registro, estado, id_zona_de_trabajo)
VALUES 
('1234567890', 'Juan Pérez Gómez', 'Calle 123, Ciudad Principal', '3001234567', '900123456-7', 'Comercial Pérez S.A.S', '2025-01-15', 'Activo', 1);

INSERT INTO clientes 
(cedula, nombre_completo_cliente, direccion, telefono, rut_nit, razon_social, fecha_registro, estado, id_zona_de_trabajo)
VALUES 
('9876543210', 'María Rodríguez López', 'Avenida Central 45, Bogotá', '3109876543', NULL, NULL, CURRENT_DATE, 'Activo', 2);

INSERT INTO clientes 
(cedula, nombre_completo_cliente, direccion, telefono, rut_nit, razon_social, fecha_registro, estado, id_zona_de_trabajo)
VALUES 
('1239874560', 'Carlos Méndez Torres', 'Carrera 10 #25-50, Medellín', '3206547891', '900654321-8', 'Distribuciones Méndez Ltda.', '2025-01-14', 'Inactivo', NULL);

INSERT INTO clientes 
(cedula, nombre_completo_cliente, direccion, telefono, rut_nit, razon_social, fecha_registro, estado, id_zona_de_trabajo)
VALUES 
('7418529630', 'Ana Sofía Gutiérrez', 'Vereda La Esperanza, Zona Rural', '3018529634', NULL, NULL, CURRENT_DATE, 'Activo', 3);



-- --------------Microservice productos--------------------------------------------
CREATE TABLE productos(
id_producto INT AUTO_INCREMENT PRIMARY KEY,
nombre_producto VARCHAR(120) NOT NULL,
precio DECIMAL(10,2) NOT NULL, 
descripcion VARCHAR(255),
cantidad_ingreso INT UNSIGNED 
);
SELECT id_producto, nombre_producto, precio FROM productos WHERE id_producto IN  (1, 2, 3);

SELECT precio FROM productos;
SELECT nombre_producto, precio FROM productos WHERE id = 1;
INSERT INTO productos (nombre_producto, precio, descripcion, cantidad_ingreso) 
VALUES 
('Laptop Dell Inspiron', 850.00, 'Laptop con procesador Intel Core i5 y 8GB de RAM', 10);

INSERT INTO productos (nombre_producto, precio, descripcion, cantidad_ingreso) 
VALUES 
('Mouse Inalámbrico Logitech', 25.99, 'Mouse ergonómico con conexión Bluetooth', 50);

INSERT INTO productos (nombre_producto, precio, descripcion, cantidad_ingreso) 
VALUES 
('Monitor Samsung 24"', 150.49, 'Monitor LED Full HD de 24 pulgadas', 20);

INSERT INTO productos (nombre_producto, precio, descripcion, cantidad_ingreso) 
VALUES 
('Teclado Mecánico Corsair', 120.00, 'Teclado mecánico RGB con switches Cherry MX', 15);

-- ----------------- microservicio preventa-devoluciones-acumulados --------------------
	CREATE TABLE preventas(
		id_preventa INT AUTO_INCREMENT PRIMARY KEY,
		id_cliente INT NOT NULL,
		id_colaborador INT NOT NULL,
		fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
		estado ENUM('Pendiente', 'Confirmada', 'Cancelada') DEFAULT 'Pendiente',
		total DECIMAL(10,2) DEFAULT 0
	);
	select * from preventas;
	CREATE TABLE detalle_preventa(
		id_detalle INT AUTO_INCREMENT PRIMARY KEY,
		id_preventa INT NOT NULL,
		id_producto INT NOT NULL,
		cantidad INT NOT NULL,
		subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0,
		FOREIGN KEY (id_preventa) REFERENCES preventas(id_preventa)
		ON DELETE CASCADE
		ON UPDATE CASCADE
	);
select * from detalle_preventa;

SELECT 
    p.id_preventa,
    p.id_colaborador,
    p.id_cliente,
    p.total,
    dp.id_producto,
    dp.cantidad,
    dp.subtotal
FROM preventaS p
INNER JOIN detalle_preventa dp ON p.id_preventa = dp.id_preventa
WHERE p.id_preventa = 1;

drop table preventas;

CREATE TABLE devoluciones(
    id_devolucion INT AUTO_INCREMENT PRIMARY KEY,
    id_preventa INT NOT NULL,
    id_producto INT NOT NULL,
    id_colaborador INT NOT NULL,
    cantidad INT UNSIGNED NOT NULL,
    fecha_devolucion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('Buen estado', 'Mal estado') DEFAULT 'Buen estado',
    FOREIGN KEY (id_preventa) REFERENCES preventas(id_preventa) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ventas(
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_preventa INT NOT NULL,
    fecha_confirmacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (id_preventa) REFERENCES preventas(id_preventa) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE detalle_venta(
    id_detalle_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT UNSIGNED NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON DELETE CASCADE ON UPDATE CASCADE
);


DELIMITER //

CREATE PROCEDURE GetPresaleDetails(
    IN in_id_preventa INT
)
BEGIN
    SELECT 
        p.id_preventa,
        p.id_cliente,
        p.id_colaborador,
        p.fecha_creacion,
        p.estado,
        p.total,
        dp.id_detalle,
        dp.id_producto,
        dp.cantidad,
        dp.subtotal
    FROM 
        preventas p
    LEFT JOIN 
        detalle_preventa dp 
    ON 
        p.id_preventa = dp.id_preventa
    WHERE 
        p.id_preventa = in_id_preventa;
END //

DELIMITER ;



