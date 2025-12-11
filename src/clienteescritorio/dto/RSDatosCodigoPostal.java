package clienteescritorio.dto;

public class RSDatosCodigoPostal {
    private boolean error;
    private String mensaje;
    private Integer idPais;
    private String pais;
    private Integer idEstado;
    private String estado;
    private Integer idMunicipio;
    private String municipio;
    private String codigoPostal;

    public RSDatosCodigoPostal() {
    }

    public RSDatosCodigoPostal(boolean error, String mensaje, Integer idPais, String pais, Integer idEstado, String estado, Integer idMunicipio, String municipio, String codigoPostal) {
        this.error = error;
        this.mensaje = mensaje;
        this.idPais = idPais;
        this.pais = pais;
        this.idEstado = idEstado;
        this.estado = estado;
        this.idMunicipio = idMunicipio;
        this.municipio = municipio;
        this.codigoPostal = codigoPostal;
    }

    public boolean isError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public String getPais() {
        return pais;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public String getEstado() {
        return estado;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
    
    
}
