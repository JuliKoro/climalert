package ar.edu.utn.ba.ddsi.climalert.services;

import ar.edu.utn.ba.ddsi.climalert.models.entities.alerta.AlertaClimatica;

public interface NotificacionService {
    void enviarAlerta(AlertaClimatica alerta);
}
