package br.com.biblioteca.models;

import java.util.Date;

public class Copia extends Obra {
    private String codigoTombo;
    private String estado;
    private Date dataAdquirida;
    private String observacoes;

    public String getCodigoTombo() {
        return codigoTombo;
    }

    public void setCodigoTombo(String codigoTombo) {
        this.codigoTombo = codigoTombo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getDataAdquirida() {
        return dataAdquirida;
    }

    public void setDataAdquirida(Date dataAdquirida) {
        this.dataAdquirida = dataAdquirida;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
