-- =====================================================================
-- LogiTrack S.A. - Datos iniciales
-- INSERT IGNORE para que se pueda reiniciar la app sin duplicar filas
-- =====================================================================

-- ------------------------- BODEGAS -------------------------
INSERT IGNORE INTO bodega (id, nombre, ubicacion, capacidad, encargado) VALUES
(1, 'Bodega Norte',  'Bogota',   5000, 'Carlos Perez'),
(2, 'Bodega Centro', 'Medellin', 3500, 'Laura Gomez'),
(3, 'Bodega Sur',    'Cali',     2000, 'Andres Rojas');

-- ------------------------- PRODUCTOS ------------------------
INSERT IGNORE INTO producto (id, nombre, categoria, stock, precio) VALUES
(1, 'Teclado Mecanico',     'Perifericos',    8,  250000.00),
(2, 'Monitor 24 Pulgadas',  'Pantallas',      4,  899000.00),
(3, 'Mouse Inalambrico',    'Perifericos',    25, 120000.00),
(4, 'Disco SSD 1TB',        'Almacenamiento', 6,  420000.00),
(5, 'Impresora Laser',      'Impresion',      3,  1500000.00);

-- ------------------------ INVENTARIO ------------------------
-- La suma por producto debe coincidir con producto.stock
INSERT IGNORE INTO inventario (id, bodega_id, producto_id, cantidad) VALUES
(1, 1, 1, 5),
(2, 1, 3, 15),
(3, 2, 2, 4),
(4, 2, 4, 6),
(5, 3, 5, 3),
(6, 3, 1, 3),
(7, 2, 3, 10);

-- Los usuarios con contrasena encriptada se crean en
-- config/DataInitializer.java porque el hash BCrypt se genera en Java.
