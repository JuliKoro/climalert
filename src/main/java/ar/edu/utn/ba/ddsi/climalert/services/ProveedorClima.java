package ar.edu.utn.ba.ddsi.climalert.services;

import ar.edu.utn.ba.ddsi.climalert.models.entities.clima.RegistroClima;

public interface ProveedorClima {
    RegistroClima obtenerClimaActual(String ubicacion);
}
