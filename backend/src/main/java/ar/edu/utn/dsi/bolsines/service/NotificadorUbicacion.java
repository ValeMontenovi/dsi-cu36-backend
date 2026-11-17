package ar.edu.utn.dsi.bolsines.service;

import ar.edu.utn.dsi.bolsines.dto.SeguimientoBolsin;

public interface NotificadorUbicacion {
    void notificar(String email, SeguimientoBolsin seguimiento);
}
