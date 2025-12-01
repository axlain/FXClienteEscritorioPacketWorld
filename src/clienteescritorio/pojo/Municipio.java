package clienteescritorio.pojo;

public class Municipio {
    private Integer idMunicipio; 
    private String nombre; 
    private Integer codigo; 
    private Integer idEstado;
    private String estado; 

    public Municipio() {
    }

    
    public Municipio(Integer idMunicipio, String nombre, Integer codigo, Integer idEstado, String estado) {
        this.idMunicipio = idMunicipio;
        this.nombre = nombre;
        this.codigo = codigo;
        this.idEstado = idEstado;
        this.estado = estado;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public String getEstado() {
        return estado;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
}
