package ar.edu.utn.dsi.bolsines.domain;

import jakarta.persistence.*;

@Entity
public class Estado {
    @Id private String nombre;
    @Column(nullable=false) private String ambito;
    private String descripcion;

    protected Estado() {}
    public Estado(String nombre) {
        this.nombre = java.util.Objects.requireNonNull(nombre);
        this.ambito = "BOLSIN";
    }
    public boolean esEnviado() { return "BOLSIN".equals(ambito) && "ENVIADO".equals(nombre); }
    public String getNombre() { return nombre; }
}
