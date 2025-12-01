package clienteescritorio.pojo;

public class Estado {
    private Integer idEstado; 
    private String nombre; 
    private Integer idPais;
    private String pais; 

    public Estado() {
    }

    public Estado(Integer idEstado, String nombre, Integer idPais, String pais) {
        this.idEstado = idEstado;
        this.nombre = nombre;
        this.idPais = idPais;
        this.pais = pais;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public String getPais() {
        return pais;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    @Override
    public String toString() {
       return nombre; 
    }
    
    
    
}
