package ar.edu.utn.ba.ddsi.climalert.services;

import ar.edu.utn.ba.ddsi.climalert.models.entities.alerta.AlertaClimatica;
import ar.edu.utn.ba.ddsi.climalert.models.entities.clima.RegistroClima;

import java.util.List;

public interface MonitoreoClimaService {

    RegistroClima registrarClimaActual(String ubicacion);

    void evaluarAlertas();

    List<RegistroClima> obtenerHistoricoClima();

    List<AlertaClimatica> obtenerHistoricoAlertas();
}
