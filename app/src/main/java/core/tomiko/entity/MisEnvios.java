package core.tomiko.entity;

public class MisEnvios {
    private String id_envio;
    private String direccionRecojo;
    private String direccionEntrega;
    private String fechaSolicitado;
    private String fechaEntregado;
    private String estado;

    public MisEnvios(String id_envio, String direccionRecojo, String direccionEntrega, String fechaSolicitado, String fechaEntregado, String estado) {
        this.id_envio = id_envio;
        this.direccionRecojo = direccionRecojo;
        this.direccionEntrega = direccionEntrega;
        this.fechaSolicitado = fechaSolicitado;
        this.fechaEntregado = fechaEntregado;
        this.estado = estado;
    }

    public String getId_envio() {
        return id_envio;
    }

    public void setId_envio(String id_envio) {
        this.id_envio = id_envio;
    }

    public String getDireccionRecojo() {
        return direccionRecojo;
    }

    public void setDireccionRecojo(String direccionRecojo) {
        this.direccionRecojo = direccionRecojo;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getFechaSolicitado() {
        return fechaSolicitado;
    }

    public void setFechaSolicitado(String fechaSolicitado) {
        this.fechaSolicitado = fechaSolicitado;
    }

    public String getFechaEntregado() {
        return fechaEntregado;
    }

    public void setFechaEntregado(String fechaEntregado) {
        this.fechaEntregado = fechaEntregado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
