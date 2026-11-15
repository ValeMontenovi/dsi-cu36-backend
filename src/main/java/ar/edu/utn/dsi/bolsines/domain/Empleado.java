package ar.edu.utn.dsi.bolsines.domain;
import jakarta.persistence.*;
@Entity public class Empleado {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String usuario;
 @ManyToOne(optional=false) private ComisionMedica comisionMedica;
 protected Empleado(){} public Empleado(String usuario,ComisionMedica comisionMedica){this.usuario=usuario;this.comisionMedica=comisionMedica;}
 public Long getId(){return id;}public String getUsuario(){return usuario;}public ComisionMedica obtenerCM(){return comisionMedica;}
}
