package cg.modelo;

public enum TipoObra {
    REPARACION("Reparación"), CONSTRUCCION("Construcción"), REMODELACION("Remodelación");

    private final String etiqueta;
    TipoObra(String etiqueta) { this.etiqueta = etiqueta; }
    @Override public String toString() { return etiqueta; }
}
