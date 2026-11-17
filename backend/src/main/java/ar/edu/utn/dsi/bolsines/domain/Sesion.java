package ar.edu.utn.dsi.bolsines.domain;

/** Sesion demo inmutable, creada para una unica peticion; no autentica al usuario. */
public class Sesion {

    private final String usuario;

    public Sesion(String usuario) {
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }
}
