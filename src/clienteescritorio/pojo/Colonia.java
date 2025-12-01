package clienteescritorio.pojo;

public class Colonia {
    private Integer idColonia; 
    private String nombre;
    private String ciudad;
    private String asentamiento;
    private Integer codigoMunicipio; 
    private Integer codigoPostal;

    public Colonia() {
    }

    public Colonia(Integer idColonia, String nombre, String ciudad, String asentamiento, Integer codigoMunicipio, Integer codigoPostal) {
        this.idColonia = idColonia;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.asentamiento = asentamiento;
        this.codigoMunicipio = codigoMunicipio;
        this.codigoPostal = codigoPostal;
    }

    public Integer getIdColonia() {
        return idColonia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getAsentamiento() {
        return asentamiento;
    }

    public Integer getCodigoMunicipio() {
        return codigoMunicipio;
    }


    public Integer getCodigoPostal() {
        return codigoPostal;
    }

    public void setIdColonia(Integer idColonia) {
        this.idColonia = idColonia;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setAsentamiento(String asentamiento) {
        this.asentamiento = asentamiento;
    }

    public void setCodigoMunicipio(Integer codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }


    public void setCodigoPostal(Integer codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
    
}
