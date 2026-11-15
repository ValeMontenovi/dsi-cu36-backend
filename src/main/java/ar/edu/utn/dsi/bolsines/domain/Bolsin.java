package ar.edu.utn.dsi.bolsines.domain;
import jakarta.persistence.*;
@Entity public class Bolsin {
 @Id private Long numero; @Column(unique=true) private String numeroPrecinto;
 @ManyToOne(optional=false) private ComisionMedica origen;
 @ManyToOne(optional=false) private ComisionMedica destino;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private EstadoBolsin estado;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private ModeloGps modeloGps;
 protected Bolsin(){}
 public Bolsin(Long numero,String numeroPrecinto,ComisionMedica origen,ComisionMedica destino,EstadoBolsin estado,ModeloGps modeloGps){this.numero=numero;this.numeroPrecinto=numeroPrecinto;this.origen=origen;this.destino=destino;this.estado=estado;this.modeloGps=modeloGps;}
 public Long getNroBolsin(){return numero;}public String getNroPrecinto(){return numeroPrecinto;}public ComisionMedica getOrigen(){return origen;}public ComisionMedica obtenerCMDestino(){return destino;}public EstadoBolsin getEstado(){return estado;}public ModeloGps getModeloGps(){return modeloGps;}
}
