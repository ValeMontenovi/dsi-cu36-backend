package ar.edu.utn.dsi.bolsines.service;
import org.springframework.stereotype.Component;
import ar.edu.utn.dsi.bolsines.dto.SeguimientoBolsin;
/** No envía mails ficticios. Falta configurar un proveedor real antes de la defensa. */
@Component public class NotificadorPendiente implements NotificadorUbicacion {
 @Override public void notificar(String email,SeguimientoBolsin seg){throw new IllegalStateException("Envío de correo no configurado. Integrar Microsoft Graph según ERS.");}
}
