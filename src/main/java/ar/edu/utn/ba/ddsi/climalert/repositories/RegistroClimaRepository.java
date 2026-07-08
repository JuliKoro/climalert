package ar.edu.utn.ba.ddsi.climalert.repositories;

import ar.edu.utn.ba.ddsi.climalert.models.entities.clima.RegistroClima;

import java.util.List;
import java.util.Optional;

public interface RegistroClimaRepository {

    RegistroClima save(RegistroClima registro);

    Optional<RegistroClima> findById(Long id);

    Optional<RegistroClima> findLatest();

    List<RegistroClima> findAll();
}
