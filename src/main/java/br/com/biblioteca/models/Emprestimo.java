package br.com.biblioteca.models;

import java.util.Date;

public class Emprestimo {
    private int id;
    private Leitor leitorId;
    private Copia copiaId;
    private Funcionario funcionarioId;
    private Date dataEmprestimo;
    private Date dataPrevistaDevoulcao;
    private Date dataDevolucao;
    private String status;

    public Date getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(Date dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public Date getDataPrevistaDevoulcao() {
        return dataPrevistaDevoulcao;
    }

    public void setDataPrevistaDevoulcao(Date dataPrevistaDevoulcao) {
        this.dataPrevistaDevoulcao = dataPrevistaDevoulcao;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
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

    public Copia getCopiaId() {
        return copiaId;
    }

    public void setCopiaId (Copia copiaId) {
        this.copiaId = copiaId;
    }

    public Funcionario getFuncionarioId() {
        return funcionarioId;
    }
    
    public void setFuncionarioId(Funcionario funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

}
