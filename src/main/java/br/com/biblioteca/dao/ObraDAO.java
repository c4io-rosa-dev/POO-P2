package br.com.biblioteca.dao;

import br.com.biblioteca.models.Obra;

import java.util.List;

public interface ObraDAO {
    void salvar(Obra obra);
    void atualizar(Obra obra);
    void deletar(int id);
    Obra buscar(int id);
    List<Obra> listar();
}
