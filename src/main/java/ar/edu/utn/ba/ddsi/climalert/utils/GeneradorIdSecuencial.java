package ar.edu.utn.ba.ddsi.climalert.utils;

public class GeneradorIdSecuencial {

    private long siguiente;

    public GeneradorIdSecuencial() {
        this(1L);
    }

    public GeneradorIdSecuencial(long valorInicial) {
        this.siguiente = valorInicial;
    }

    public synchronized long siguiente() {
        return siguiente++;
    }
}
