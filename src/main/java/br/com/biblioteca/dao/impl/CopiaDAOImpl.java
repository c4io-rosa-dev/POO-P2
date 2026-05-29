package br.com.biblioteca.dao.impl;

import br.com.biblioteca.dao.CopiaDAO;
import br.com.biblioteca.exception.BibliotecaException;
import br.com.biblioteca.models.Copia;
import br.com.biblioteca.models.Obra;
import br.com.biblioteca.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação JDBC do CopiaDAO.
 *
 * AGREGAÇÃO: a tabela copia tem FK obra_id. Ao gravar usamos
 * copia.getObra().getId(); ao ler fazemos JOIN com obra e populamos
 * copia.setObra(obra).
 */
public class CopiaDAOImpl implements CopiaDAO {

    @Override
    public void salvar(Copia copia) {
        String sql = "INSERT INTO copia (obra_id, codigo_tombo, estado, adquirida_em, observacoes) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, copia.getObra().getId());
            ps.setString(2, copia.getCodigoTombo());
            ps.setString(3, copia.getEstado());
            ps.setDate(4, copia.getDataAdquirida() != null
                    ? toSqlDate(copia.getDataAdquirida())
                    : new Date(System.currentTimeMillis()));
            ps.setString(5, copia.getObservacoes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    copia.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao salvar cópia: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Copia copia) {
        String sql = "UPDATE copia SET obra_id=?, codigo_tombo=?, estado=?, adquirida_em=?, observacoes=? "
                + "WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, copia.getObra().getId());
            ps.setString(2, copia.getCodigoTombo());
            ps.setString(3, copia.getEstado());
            ps.setDate(4, toSqlDate(copia.getDataAdquirida()));
            ps.setString(5, copia.getObservacoes());
            ps.setInt(6, copia.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao atualizar cópia: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM copia WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao deletar cópia: " + e.getMessage(), e);
        }
    }

    @Override
    public Copia buscar(int id) {
        String sql = baseSelect() + " WHERE c.id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao buscar cópia: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Copia> listar() {
        String sql = baseSelect() + " ORDER BY c.codigo_tombo";
        List<Copia> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao listar cópias: " + e.getMessage(), e);
        }
    }

    private String baseSelect() {
        return "SELECT c.id, c.codigo_tombo, c.estado, c.adquirida_em, c.observacoes, "
                + "o.id AS obra_id, o.titulo, o.autor, o.editora, o.ano_publicacao, o.isbn, o.categoria, o.tipo "
                + "FROM copia c JOIN obra o ON o.id = c.obra_id";
    }

    private Copia montar(ResultSet rs) throws SQLException {
        Obra o = new Obra();
        o.setId(rs.getInt("obra_id"));
        o.setTitulo(rs.getString("titulo"));
        o.setAutor(rs.getString("autor"));
        o.setEditora(rs.getString("editora"));
        o.setAnoPublicacao(rs.getInt("ano_publicacao"));
        o.setIsbn(rs.getString("isbn"));
        o.setCategoria(rs.getString("categoria"));
        o.setTipo(rs.getString("tipo"));

        Copia copia = new Copia();
        copia.setId(rs.getInt("id"));
        copia.setObra(o);
        copia.setCodigoTombo(rs.getString("codigo_tombo"));
        copia.setEstado(rs.getString("estado"));
        copia.setDataAdquirida(rs.getDate("adquirida_em"));
        copia.setObservacoes(rs.getString("observacoes"));
        return copia;
    }

    private static Date toSqlDate(java.util.Date d) {
        return d != null ? new Date(d.getTime()) : null;
    }
}
