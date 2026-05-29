package br.com.biblioteca.dao;

import br.com.biblioteca.models.Reserva;

import java.util.List;

public interface ReservaDAO {
    void salvar(Reserva reserva);
    void atualizar(Reserva reserva);
    void deletar(int id);
    Reserva buscar(int id);
    List<Reserva> listar();
}
