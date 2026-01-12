package clienteescritorio.interfaz;

public interface INotificador {
    void notificarOperacionExitosa(String operacion, String nombre);

    default void notificarFotoActualizada(int idColaborador) {
       
    }
}
