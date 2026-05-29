package br.com.biblioteca.models;

import java.util.Date;

public class Reserva {
    private int id;
    private Leitor leitorId;
    private Obra obraId;
    private Date dataReserva;
    private Date dataExpiracao;
    private String status;
    public Date getDataReserva() {
        return dataReserva;
    }
    public void setDataReserva(Date dataReserva) {
        this.dataReserva = dataReserva;
    }
    public Date getDataExpiracao() {
        return dataExpiracao;
    }
    public void setDataExpiracao(Date dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Leitor getLeitorId() {
        return leitorId;
    }
    public void setLeitorId(Leitor leitorId) {
        this.leitorId = leitorId;
    }
    public Obra getObraId() {
        return obraId;
    }
    public void setObraId(Obra obraId) {
        this.obraId = obraId;
    }

    
}
