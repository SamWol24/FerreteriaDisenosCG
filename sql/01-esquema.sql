CREATE DATABASE IF NOT EXISTS ferreteria_disenos_cg CHARACTER SET utf8mb4;
USE ferreteria_disenos_cg;

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS marca (
    id_marca INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS producto (
    id_producto INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL,
    descripcion VARCHAR(255),
    precio_compra DECIMAL(12,2) NOT NULL CHECK (precio_compra >= 0),
    precio_venta DECIMAL(12,2) NOT NULL CHECK (precio_venta > 0),
    unidad_medida VARCHAR(30) NOT NULL,
    id_categoria INT NOT NULL,
    id_marca INT,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    FOREIGN KEY (id_marca) REFERENCES marca(id_marca)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventario (
    id_inventario INT PRIMARY KEY AUTO_INCREMENT,
    id_producto INT NOT NULL UNIQUE,
    cantidad INT NOT NULL CHECK (cantidad >= 0),
    stock_minimo INT NOT NULL CHECK (stock_minimo >= 0),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cotizacion (
    id_cotizacion INT PRIMARY KEY AUTO_INCREMENT,
    cliente VARCHAR(120) NOT NULL,
    tipo_obra ENUM('Reparación','Construcción','Remodelación') NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_cotizacion (
    id_cotizacion INT NOT NULL,
    id_producto INT NOT NULL,
    nombre_producto VARCHAR(120) NOT NULL,
    unidad VARCHAR(30) NOT NULL,
    cantidad DECIMAL(12,3) NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(12,2) NOT NULL CHECK (precio_unitario > 0),
    PRIMARY KEY (id_cotizacion, id_producto),
    FOREIGN KEY (id_cotizacion) REFERENCES cotizacion(id_cotizacion),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
) ENGINE=InnoDB;
