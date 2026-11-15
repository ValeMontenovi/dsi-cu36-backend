package ar.edu.utn.dsi.bolsines.domain;
import jakarta.persistence.*;
@Entity public class Empleado {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String usuario;
 @ManyToOne(optional=false) private ComisionMedica comisionMedica;
 protected Empleado(){} public Empleado(String usuario,ComisionMedica comisionMedica){this.usuario=usuario;this.comisionMedica=comisionMedica;}
 public boolean esTuUsuario(String usuario){return java.util.Objects.equals(this.usuario,usuario);}
 public Long getId(){return id;}public String getUsuario(){return usuario;}public String obtenerCM(){return comisionMedica.getCodigo();}
}
