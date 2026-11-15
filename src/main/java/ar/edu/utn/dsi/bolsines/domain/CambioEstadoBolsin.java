package ar.edu.utn.dsi.bolsines.domain;

import java.time.OffsetDateTime;
import jakarta.persistence.*;

@Entity
public class CambioEstadoBolsin {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    // null en registros importados: el sistema anterior no conservaba esta fecha.
    private OffsetDateTime fechaHoraInicio;
    private OffsetDateTime fechaHoraFin;
    @Column(nullable=false) private boolean importado;
    @ManyToOne(optional=false, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    private Estado estado;

    protected CambioEstadoBolsin() {}
    public CambioEstadoBolsin(OffsetDateTime inicio, OffsetDateTime fin, Estado estado) {
        this.fechaHoraInicio = java.util.Objects.requireNonNull(inicio);
        this.fechaHoraFin = fin;
        this.estado = java.util.Objects.requireNonNull(estado);
    }
    public boolean sosActual() { return fechaHoraFin == null; }
    public boolean sosEnviado() { return estado.esEnviado(); }
    public OffsetDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public OffsetDateTime getFechaHoraFin() { return fechaHoraFin; }
    public boolean isImportado() { return importado; }
}
