package br.com.biblioteca.dao;

import br.com.biblioteca.models.Emprestimo;

import java.util.List;

public interface EmprestimoDAO {
    void salvar(Emprestimo emprestimo);
    void atualizar(Emprestimo emprestimo);
    void deletar(int id);
    Emprestimo buscar(int id);
    List<Emprestimo> listar();
}
