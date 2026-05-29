package br.com.biblioteca.dao.impl;

import br.com.biblioteca.dao.ObraDAO;
import br.com.biblioteca.exception.BibliotecaException;
import br.com.biblioteca.models.Obra;
import br.com.biblioteca.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação JDBC do ObraDAO. Tabela única "obra".
 */
public class ObraDAOImpl implements ObraDAO {

    @Override
    public void salvar(Obra obra) {
        String sql = "INSERT INTO obra (titulo, autor, editora, ano_publicacao, isbn, categoria, tipo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, obra);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    obra.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao salvar obra: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Obra obra) {
        String sql = "UPDATE obra SET titulo=?, autor=?, editora=?, ano_publicacao=?, isbn=?, categoria=?, tipo=? "
                + "WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencher(ps, obra);
            ps.setInt(8, obra.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao atualizar obra: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM obra WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao deletar obra: " + e.getMessage(), e);
        }
    }

    @Override
    public Obra buscar(int id) {
        String sql = "SELECT id, titulo, autor, editora, ano_publicacao, isbn, categoria, tipo "
                + "FROM obra WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao buscar obra: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Obra> listar() {
        String sql = "SELECT id, titulo, autor, editora, ano_publicacao, isbn, categoria, tipo "
                + "FROM obra ORDER BY titulo";
        List<Obra> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao listar obras: " + e.getMessage(), e);
        }
    }

    private void preencher(PreparedStatement ps, Obra obra) throws SQLException {
        ps.setString(1, obra.getTitulo());
        ps.setString(2, obra.getAutor());
        ps.setString(3, obra.getEditora());
        // Ano 0 (não informado) vira NULL — o banco exige ano entre 1000 e 2100 ou NULL.
        if (obra.getAnoPublicacao() > 0) {
            ps.setInt(4, obra.getAnoPublicacao());
        } else {
            ps.setNull(4, Types.INTEGER);
        }
        ps.setString(5, obra.getIsbn());
        ps.setString(6, obra.getCategoria());
        ps.setString(7, obra.getTipo());
    }

    private Obra montar(ResultSet rs) throws SQLException {
        Obra o = new Obra();
        o.setId(rs.getInt("id"));
        o.setTitulo(rs.getString("titulo"));
        o.setAutor(rs.getString("autor"));
        o.setEditora(rs.getString("editora"));
        o.setAnoPublicacao(rs.getInt("ano_publicacao"));
        o.setIsbn(rs.getString("isbn"));
        o.setCategoria(rs.getString("categoria"));
        o.setTipo(rs.getString("tipo"));
        return o;
    }
}
