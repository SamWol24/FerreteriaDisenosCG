USE ferreteria_disenos_cg;
START TRANSACTION;
INSERT IGNORE INTO categoria (nombre) VALUES ('Construcción'), ('Obra blanca'), ('Electricidad'), ('Diseño');
INSERT IGNORE INTO marca (nombre) VALUES ('Genérica'), ('Argos');
INSERT IGNORE INTO producto
    (codigo, nombre, precio_compra, precio_venta, unidad_medida, id_categoria, id_marca)
SELECT 'LAD-01', 'Ladrillo muro', 800, 1200, 'unidad', c.id_categoria, m.id_marca
FROM categoria c CROSS JOIN marca m WHERE c.nombre='Construcción' AND m.nombre='Genérica'
UNION ALL
SELECT 'CEM-01', 'Cemento gris', 25000, 32000, 'bolsa', c.id_categoria, m.id_marca
FROM categoria c CROSS JOIN marca m WHERE c.nombre='Construcción' AND m.nombre='Argos'
UNION ALL
SELECT 'ARE-01', 'Arena', 65000, 90000, 'm³', c.id_categoria, m.id_marca
FROM categoria c CROSS JOIN marca m WHERE c.nombre='Construcción' AND m.nombre='Genérica';
INSERT IGNORE INTO inventario (id_producto, cantidad, stock_minimo)
SELECT id_producto, CASE codigo WHEN 'LAD-01' THEN 1000 ELSE 20 END, 5
FROM producto WHERE codigo IN ('LAD-01','CEM-01','ARE-01');
COMMIT;
