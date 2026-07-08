package ar.edu.utn.ba.ddsi.climalert.repositories;

import ar.edu.utn.ba.ddsi.climalert.models.entities.alerta.AlertaClimatica;

import java.util.List;
import java.util.Optional;

public interface AlertaRepository {

    AlertaClimatica save(AlertaClimatica alerta);

    Optional<AlertaClimatica> findById(Long id);

    List<AlertaClimatica> findAll();
}
