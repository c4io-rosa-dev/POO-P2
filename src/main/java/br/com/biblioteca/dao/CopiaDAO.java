package br.com.biblioteca.dao;

import br.com.biblioteca.models.Copia;

public interface CopiaDAO {
    void salvar(Copia copia);
    Copia buscar(int id);
    void atualizar(Copia copia);
    void deletar(int id);
}
