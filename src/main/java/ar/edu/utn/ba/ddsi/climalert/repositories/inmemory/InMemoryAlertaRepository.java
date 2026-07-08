package ar.edu.utn.ba.ddsi.climalert.repositories.inmemory;

import ar.edu.utn.ba.ddsi.climalert.models.entities.alerta.AlertaClimatica;
import ar.edu.utn.ba.ddsi.climalert.repositories.AlertaRepository;
import ar.edu.utn.ba.ddsi.climalert.utils.GeneradorIdSecuencial;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryAlertaRepository implements AlertaRepository {

    private final List<AlertaClimatica> alertas = new ArrayList<>();
    private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

    @Override
    public synchronized List<AlertaClimatica> findAll() {
        return new ArrayList<>(alertas);
    }

    @Override
    public synchronized Optional<AlertaClimatica> findById(Long id) {
        return alertas.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    @Override
    public synchronized AlertaClimatica save(AlertaClimatica alerta) {
        if (alerta.getId() == null) {
            alerta.setId(generadorId.siguiente());
            alertas.add(alerta);
            return alerta;
        }
        alertas.removeIf(a -> a.getId().equals(alerta.getId()));
        alertas.add(alerta);
        return alerta;
    }
}
