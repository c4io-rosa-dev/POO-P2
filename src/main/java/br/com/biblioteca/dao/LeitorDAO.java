package br.com.biblioteca.dao;

import br.com.biblioteca.models.Leitor;

public interface LeitorDAO {
    void salvar(Leitor leitor);
    void atualizar(Leitor leitor);
    void deletar(int id);
    Leitor buscar(int id);
}
