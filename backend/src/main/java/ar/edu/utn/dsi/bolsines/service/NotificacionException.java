package ar.edu.utn.dsi.bolsines.service;

/** El mail de ubicación no se pudo enviar (por ejemplo, el servidor de correo rechazó la conexión). */
public class NotificacionException extends RuntimeException {

    public NotificacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
