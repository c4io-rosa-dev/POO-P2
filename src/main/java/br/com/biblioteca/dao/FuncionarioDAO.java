package br.com.biblioteca.dao;

import br.com.biblioteca.models.Funcionario;

public interface FuncionarioDAO {
    void salvar(Funcionario funcionario);
    void atualizar(Funcionario funcionario);
    void deletar(int id);
    Funcionario buscar(int id);
}
