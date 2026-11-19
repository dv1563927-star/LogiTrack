-- =====================================================================
-- LogiTrack S.A. - Creacion de tablas
-- Se ejecuta al arrancar la aplicacion (spring.sql.init.mode=always)
-- =====================================================================

-- Usuarios del sistema (ADMIN / EMPLEADO)
CREATE TABLE IF NOT EXISTS usuario (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre   VARCHAR(100) NOT NULL,
    telefono VARCHAR(20)  NOT NULL,
    rol      VARCHAR(20)  NOT NULL,
    estado   VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO'
);

-- Bodegas distribuidas en las ciudades
CREATE TABLE IF NOT EXISTS bodega (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(100) NOT NULL,
    capacidad INT          NOT NULL,
    encargado VARCHAR(100) NOT NULL
);

-- Catalogo de productos
CREATE TABLE IF NOT EXISTS producto (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100)   NOT NULL,
    categoria VARCHAR(50)    NOT NULL,
    stock     INT            NOT NULL DEFAULT 0,
    precio    DECIMAL(15, 2) NOT NULL
);

-- Stock de cada producto dentro de cada bodega
CREATE TABLE IF NOT EXISTS inventario (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    bodega_id  BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad   INT    NOT NULL DEFAULT 0,
    CONSTRAINT fk_inventario_bodega FOREIGN KEY (bodega_id) REFERENCES bodega (id),
    CONSTRAINT fk_inventario_producto FOREIGN KEY (producto_id) REFERENCES producto (id),
    CONSTRAINT uq_inventario UNIQUE (bodega_id, producto_id)
);

-- Cabecera del movimiento de inventario
CREATE TABLE IF NOT EXISTS movimiento (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha            DATETIME    NOT NULL,
    tipo             VARCHAR(20) NOT NULL,
    usuario_id       BIGINT      NOT NULL,
    bodega_origen_id  BIGINT     NULL,
    bodega_destino_id BIGINT     NULL,
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_movimiento_origen FOREIGN KEY (bodega_origen_id) REFERENCES bodega (id),
    CONSTRAINT fk_movimiento_destino FOREIGN KEY (bodega_destino_id) REFERENCES bodega (id)
);

-- Productos y cantidades de cada movimiento
CREATE TABLE IF NOT EXISTS detalle_movimiento (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id BIGINT NOT NULL,
    producto_id   BIGINT NOT NULL,
    cantidad      INT    NOT NULL,
    CONSTRAINT fk_detalle_movimiento FOREIGN KEY (movimiento_id) REFERENCES movimiento (id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto (id)
);

-- Registro de auditoria de todos los cambios
CREATE TABLE IF NOT EXISTS auditoria (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_operacion    VARCHAR(20) NOT NULL,
    fecha_hora        DATETIME    NOT NULL,
    usuario           VARCHAR(50) NOT NULL,
    entidad_afectada  VARCHAR(50) NOT NULL,
    valores_anteriores TEXT       NULL,
    valores_nuevos     TEXT       NULL
);
