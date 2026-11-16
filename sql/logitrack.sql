-- =====================================================================
-- LogiTrack S.A. - Script completo de la base de datos
-- Pegar TODO esto en MySQL Workbench / phpMyAdmin y ejecutar de una vez.
-- Se puede ejecutar varias veces: borra y vuelve a crear todo.
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------- BASE DE DATOS ---------------------------
CREATE DATABASE IF NOT EXISTS logitrack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE logitrack;

-- ------------------------- LIMPIAR --------------------------------
DROP TABLE IF EXISTS auditoria;
DROP TABLE IF EXISTS detalle_movimiento;
DROP TABLE IF EXISTS movimiento;
DROP TABLE IF EXISTS inventario;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS bodega;
DROP TABLE IF EXISTS usuario;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- TABLAS
-- =====================================================================

-- Usuarios del sistema
CREATE TABLE usuario (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol      VARCHAR(20)  NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Bodegas
CREATE TABLE bodega (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(100) NOT NULL,
    capacidad INT          NOT NULL,
    encargado VARCHAR(100) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Productos
CREATE TABLE producto (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100)   NOT NULL,
    categoria VARCHAR(50)    NOT NULL,
    stock     INT            NOT NULL DEFAULT 0,
    precio    DECIMAL(15, 2) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Stock de cada producto en cada bodega
CREATE TABLE inventario (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    bodega_id   BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad    INT    NOT NULL DEFAULT 0,
    CONSTRAINT fk_inventario_bodega   FOREIGN KEY (bodega_id)   REFERENCES bodega (id),
    CONSTRAINT fk_inventario_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT uq_inventario UNIQUE (bodega_id, producto_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Cabecera del movimiento
CREATE TABLE movimiento (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha             DATETIME    NOT NULL,
    tipo              VARCHAR(20) NOT NULL,
    usuario_id        BIGINT      NOT NULL,
    bodega_origen_id  BIGINT      NULL,
    bodega_destino_id BIGINT      NULL,
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (usuario_id)        REFERENCES usuario (id),
    CONSTRAINT fk_movimiento_origen  FOREIGN KEY (bodega_origen_id)  REFERENCES bodega (id),
    CONSTRAINT fk_movimiento_destino FOREIGN KEY (bodega_destino_id) REFERENCES bodega (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Productos y cantidades de cada movimiento
CREATE TABLE detalle_movimiento (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id BIGINT NOT NULL,
    producto_id   BIGINT NOT NULL,
    cantidad      INT    NOT NULL,
    CONSTRAINT fk_detalle_movimiento FOREIGN KEY (movimiento_id) REFERENCES movimiento (id),
    CONSTRAINT fk_detalle_producto   FOREIGN KEY (producto_id)   REFERENCES producto (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Auditoria de cambios
CREATE TABLE auditoria (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_operacion     VARCHAR(20) NOT NULL,
    fecha_hora         DATETIME    NOT NULL,
    usuario            VARCHAR(50) NOT NULL,
    entidad_afectada   VARCHAR(50) NOT NULL,
    valores_anteriores TEXT        NULL,
    valores_nuevos     TEXT        NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- =====================================================================
-- DATOS DE PRUEBA
-- =====================================================================

-- Usuarios (contrasena en texto plano, solo para el ejercicio academico)
INSERT INTO usuario (id, username, password, rol) VALUES
(1, 'admin',    'admin123',    'ADMIN'),
(2, 'empleado', 'empleado123', 'EMPLEADO');

-- Bodegas
INSERT INTO bodega (id, nombre, ubicacion, capacidad, encargado) VALUES
(1, 'Bodega Norte',  'Bogota',   5000, 'Carlos Perez'),
(2, 'Bodega Centro', 'Medellin', 3500, 'Laura Gomez'),
(3, 'Bodega Sur',    'Cali',     2000, 'Andres Rojas');

-- Productos
INSERT INTO producto (id, nombre, categoria, stock, precio) VALUES
(1, 'Teclado Mecanico',    'Perifericos',    8,  250000.00),
(2, 'Monitor 24 Pulgadas', 'Pantallas',      4,  899000.00),
(3, 'Mouse Inalambrico',   'Perifericos',    25, 120000.00),
(4, 'Disco SSD 1TB',       'Almacenamiento', 6,  420000.00),
(5, 'Impresora Laser',     'Impresion',      3,  1500000.00);

-- Inventario por bodega (la suma de cada producto da su producto.stock)
INSERT INTO inventario (id, bodega_id, producto_id, cantidad) VALUES
(1, 1, 1, 5),
(2, 1, 3, 15),
(3, 2, 2, 4),
(4, 2, 4, 6),
(5, 3, 5, 3),
(6, 3, 1, 3),
(7, 2, 3, 10);

-- =====================================================================
-- LISTO. Ahora crea la base con:
--   mysql -u campus2023 -p < logitrack.sql
-- o pega todo esto en MySQL Workbench.
-- =====================================================================
