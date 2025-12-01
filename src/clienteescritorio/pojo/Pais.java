package clienteescritorio.pojo;

public class Pais {
    private Integer idPais; 
    private String nombre; 

    public Pais() {
    }

    public Pais(Integer idPais, String nombre) {
        this.idPais = idPais;
        this.nombre = nombre;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public String getNombre() {
        return nombre;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre ;
    }
    
}
