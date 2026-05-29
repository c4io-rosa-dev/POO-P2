package br.com.biblioteca.dao;

import br.com.biblioteca.models.Copia;

import java.util.List;

public interface CopiaDAO {
    void salvar(Copia copia);
    Copia buscar(int id);
    void atualizar(Copia copia);
    void deletar(int id);
    List<Copia> listar();
}
