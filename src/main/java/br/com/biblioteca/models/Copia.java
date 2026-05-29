package br.com.biblioteca.models;

import java.util.Date;

/**
 * Cópia física de uma Obra.
 *
 * AGREGAÇÃO (não herança): uma Copia NÃO é um tipo de Obra — ela PERTENCE a
 * uma Obra. Por isso guarda uma referência {@code obra} em vez de estender Obra.
 * Bate com o banco, onde copia tem FK obra_id apontando para obra.
 */
public class Copia {
    private int id;
    private Obra obra;            // agregação: a obra à qual esta cópia pertence
    private String codigoTombo;
    private String estado;
    private Date dataAdquirida;
    private String observacoes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

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

    // Usado na exibição em JComboBox na UI (ex.: escolher a cópia num empréstimo).
    @Override
    public String toString() {
        String titulo = (obra != null) ? obra.getTitulo() : "?";
        return codigoTombo + " (" + titulo + ")";
    }
}
