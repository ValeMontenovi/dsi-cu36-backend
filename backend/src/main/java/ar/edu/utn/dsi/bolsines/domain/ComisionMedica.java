package ar.edu.utn.dsi.bolsines.domain;

import jakarta.persistence.*;

@Entity
public class ComisionMedica {

    @Id
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String emailGerente;

    protected ComisionMedica() {}

    public ComisionMedica(String codigo, String nombre, String emailGerente) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.emailGerente = emailGerente;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmailGerente() {
        return emailGerente;
    }
}
