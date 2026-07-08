package ar.edu.utn.ba.ddsi.climalert.repositories.inmemory;

import ar.edu.utn.ba.ddsi.climalert.models.entities.clima.RegistroClima;
import ar.edu.utn.ba.ddsi.climalert.repositories.RegistroClimaRepository;
import ar.edu.utn.ba.ddsi.climalert.utils.GeneradorIdSecuencial;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryRegistroClimaRepository implements RegistroClimaRepository {

    private final List<RegistroClima> registros = new ArrayList<>();
    private final GeneradorIdSecuencial generadorId = new GeneradorIdSecuencial();

    @Override
    public synchronized List<RegistroClima> findAll() {
        return new ArrayList<>(registros);
    }

    @Override
    public synchronized Optional<RegistroClima> findById(Long id) {
        return registros.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public synchronized Optional<RegistroClima> findLatest() {
        if (registros.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(registros.get(registros.size() - 1));
    }

    @Override
    public synchronized RegistroClima save(RegistroClima registro) {
        if (registro.getId() == null) {
            registro.setId(generadorId.siguiente());
            registros.add(registro);
            return registro;
        }
        registros.removeIf(r -> r.getId().equals(registro.getId()));
        registros.add(registro);
        return registro;
    }
}
