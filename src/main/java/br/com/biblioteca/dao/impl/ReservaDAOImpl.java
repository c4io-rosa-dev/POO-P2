package br.com.biblioteca.dao.impl;

import br.com.biblioteca.dao.ReservaDAO;
import br.com.biblioteca.exception.BibliotecaException;
import br.com.biblioteca.models.Leitor;
import br.com.biblioteca.models.Obra;
import br.com.biblioteca.models.Reserva;
import br.com.biblioteca.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação JDBC do ReservaDAO.
 *
 * ASSOCIAÇÃO: reserva liga leitor + obra (reserva o título, não a cópia).
 * As datas são TIMESTAMP no banco.
 */
public class ReservaDAOImpl implements ReservaDAO {

    @Override
    public void salvar(Reserva r) {
        String sql = "INSERT INTO reserva (leitor_id, obra_id, data_reserva, data_expiracao, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getLeitorId().getId());
            ps.setInt(2, r.getObraId().getId());
            ps.setTimestamp(3, r.getDataReserva() != null
                    ? toTimestamp(r.getDataReserva())
                    : new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(4, toTimestamp(r.getDataExpiracao()));
            ps.setString(5, r.getStatus() != null ? r.getStatus() : "ATIVA");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao salvar reserva: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void atualizar(Reserva r) {
        String sql = "UPDATE reserva SET leitor_id=?, obra_id=?, data_reserva=?, data_expiracao=?, status=? "
                + "WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, r.getLeitorId().getId());
            ps.setInt(2, r.getObraId().getId());
            ps.setTimestamp(3, toTimestamp(r.getDataReserva()));
            ps.setTimestamp(4, toTimestamp(r.getDataExpiracao()));
            ps.setString(5, r.getStatus());
            ps.setInt(6, r.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao atualizar reserva: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM reserva WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao deletar reserva: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Reserva buscar(int id) {
        String sql = baseSelect() + " WHERE r.id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao buscar reserva: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Reserva> listar() {
        String sql = baseSelect() + " ORDER BY r.data_reserva DESC, r.id DESC";
        List<Reserva> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao listar reservas: " + ex.getMessage(), ex);
        }
    }

    private String baseSelect() {
        return "SELECT r.id, r.data_reserva, r.data_expiracao, r.status, "
                + "li.id AS leitor_id, pl.nome AS leitor_nome, pl.matricula AS leitor_matricula, "
                + "o.id AS obra_id, o.titulo AS obra_titulo, o.autor AS obra_autor "
                + "FROM reserva r "
                + "JOIN leitor li ON li.id = r.leitor_id "
                + "JOIN pessoa pl ON pl.id = li.id "
                + "JOIN obra o ON o.id = r.obra_id";
    }

    private Reserva montar(ResultSet rs) throws SQLException {
        Leitor leitor = new Leitor();
        leitor.setId(rs.getInt("leitor_id"));
        leitor.setNome(rs.getString("leitor_nome"));
        leitor.setMatricula(rs.getString("leitor_matricula"));

        Obra obra = new Obra();
        obra.setId(rs.getInt("obra_id"));
        obra.setTitulo(rs.getString("obra_titulo"));
        obra.setAutor(rs.getString("obra_autor"));

        Reserva r = new Reserva();
        r.setId(rs.getInt("id"));
        r.setLeitorId(leitor);
        r.setObraId(obra);
        r.setDataReserva(rs.getTimestamp("data_reserva"));
        r.setDataExpiracao(rs.getTimestamp("data_expiracao"));
        r.setStatus(rs.getString("status"));
        return r;
    }

    private static Timestamp toTimestamp(java.util.Date d) {
        return d != null ? new Timestamp(d.getTime()) : null;
    }
}
