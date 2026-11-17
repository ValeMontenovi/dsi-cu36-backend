package ar.edu.utn.dsi.bolsines.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Bolsin {

    @Id
    private Long numero;

    @Column(unique = true)
    private String numeroPrecinto;

    @ManyToOne(optional = false)
    private ComisionMedica origen;

    @ManyToOne(optional = false)
    private ComisionMedica destino;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bolsin_numero", nullable = false)
    private List<CambioEstadoBolsin> cambioEstado = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModeloGps modeloGps;

    protected Bolsin() {}

    public Bolsin(
        Long numero,
        String numeroPrecinto,
        ComisionMedica origen,
        ComisionMedica destino,
        List<CambioEstadoBolsin> cambioEstado,
        ModeloGps modeloGps
    ) {
        this.numero = numero;
        this.numeroPrecinto = numeroPrecinto;
        this.origen = origen;
        this.destino = destino;
        this.cambioEstado = new ArrayList<>(cambioEstado);
        this.modeloGps = modeloGps;
    }

    public boolean esTuCMOrigen(String codigoCM) {
        return origen.getCodigo().equals(codigoCM);
    }

    public boolean sosEnviado() {
        List<CambioEstadoBolsin> actuales = cambioEstado
            .stream()
            .filter(CambioEstadoBolsin::sosActual)
            .toList();
        if (actuales.size() != 1) throw new InconsistenciaEstadoBolsinException(
            numero,
            actuales.size()
        );
        return actuales.get(0).sosEnviado();
    }

    public Long getNroBolsin() {
        return numero;
    }

    public String getNroPrecinto() {
        return numeroPrecinto;
    }

    public ComisionMedica getOrigen() {
        return origen;
    }

    public ComisionMedica obtenerCMDestino() {
        return destino;
    }

    public ModeloGps getModeloGps() {
        return modeloGps;
    }

    public List<CambioEstadoBolsin> getCambioEstado() {
        return List.copyOf(cambioEstado);
    }
}
