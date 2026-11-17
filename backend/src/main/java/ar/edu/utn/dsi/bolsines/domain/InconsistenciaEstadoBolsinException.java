package ar.edu.utn.dsi.bolsines.domain;

public class InconsistenciaEstadoBolsinException extends RuntimeException {

    public InconsistenciaEstadoBolsinException(Long numero, int cantidad) {
        super(
            "Inconsistencia en bolsin " +
                numero +
                ": se esperaba un cambio de estado actual, encontrados " +
                cantidad
        );
    }
}
