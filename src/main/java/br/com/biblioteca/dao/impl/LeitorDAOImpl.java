package br.com.biblioteca.dao.impl;

import br.com.biblioteca.dao.LeitorDAO;
import br.com.biblioteca.exception.BibliotecaException;
import br.com.biblioteca.models.Leitor;
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
 * Implementação JDBC do LeitorDAO.
 *
 * HERANÇA no banco (Class Table Inheritance): os dados comuns ficam em "pessoa"
 * e os específicos em "leitor". Por isso salvar/atualizar mexem nas DUAS tabelas
 * dentro de uma transação, e buscar/listar fazem JOIN entre elas.
 */
public class LeitorDAOImpl implements LeitorDAO {

    @Override
    public void salvar(Leitor leitor) {
        String sqlPessoa = "INSERT INTO pessoa (nome, cpf, email, telefone, matricula, data_nascimento) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlLeitor = "INSERT INTO leitor (id, ativo, inscrito_em) VALUES (?, ?, ?)";

        Connection c = null;
        try {
            c = ConnectionFactory.getConnection();
            c.setAutoCommit(false);

            int idGerado;
            try (PreparedStatement ps = c.prepareStatement(sqlPessoa, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, leitor.getNome());
                ps.setString(2, leitor.getCpf());
                ps.setString(3, leitor.getEmail());
                ps.setString(4, leitor.getTelefone());
                ps.setString(5, leitor.getMatricula());
                ps.setDate(6, toSqlDate(leitor.getDataNascimento()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    idGerado = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(sqlLeitor)) {
                ps.setInt(1, idGerado);
                ps.setBoolean(2, leitor.isAtivo());
                ps.setDate(3, leitor.getDataCadastro() != null
                        ? toSqlDate(leitor.getDataCadastro())
                        : new Date(System.currentTimeMillis()));
                ps.executeUpdate();
            }

            c.commit();
            leitor.setId(idGerado);
        } catch (SQLException e) {
            rollback(c);
            throw new BibliotecaException("Erro ao salvar leitor: " + e.getMessage(), e);
        } finally {
            fechar(c);
        }
    }

    @Override
    public void atualizar(Leitor leitor) {
        String sqlPessoa = "UPDATE pessoa SET nome=?, cpf=?, email=?, telefone=?, matricula=?, data_nascimento=? "
                + "WHERE id=?";
        String sqlLeitor = "UPDATE leitor SET ativo=?, inscrito_em=? WHERE id=?";

        Connection c = null;
        try {
            c = ConnectionFactory.getConnection();
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(sqlPessoa)) {
                ps.setString(1, leitor.getNome());
                ps.setString(2, leitor.getCpf());
                ps.setString(3, leitor.getEmail());
                ps.setString(4, leitor.getTelefone());
                ps.setString(5, leitor.getMatricula());
                ps.setDate(6, toSqlDate(leitor.getDataNascimento()));
                ps.setInt(7, leitor.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(sqlLeitor)) {
                ps.setBoolean(1, leitor.isAtivo());
                ps.setDate(2, toSqlDate(leitor.getDataCadastro()));
                ps.setInt(3, leitor.getId());
                ps.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            rollback(c);
            throw new BibliotecaException("Erro ao atualizar leitor: " + e.getMessage(), e);
        } finally {
            fechar(c);
        }
    }

    @Override
    public void deletar(int id) {
        // Apaga em pessoa; o ON DELETE CASCADE remove a linha correspondente em leitor.
        String sql = "DELETE FROM pessoa WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao deletar leitor: " + e.getMessage(), e);
        }
    }

    @Override
    public Leitor buscar(int id) {
        String sql = "SELECT p.id, p.nome, p.cpf, p.email, p.telefone, p.matricula, p.data_nascimento, "
                + "l.ativo, l.inscrito_em "
                + "FROM leitor l JOIN pessoa p ON p.id = l.id WHERE l.id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao buscar leitor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Leitor> listar() {
        String sql = "SELECT p.id, p.nome, p.cpf, p.email, p.telefone, p.matricula, p.data_nascimento, "
                + "l.ativo, l.inscrito_em "
                + "FROM leitor l JOIN pessoa p ON p.id = l.id ORDER BY p.nome";
        List<Leitor> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao listar leitores: " + e.getMessage(), e);
        }
    }

    private Leitor montar(ResultSet rs) throws SQLException {
        Leitor l = new Leitor();
        l.setId(rs.getInt("id"));
        l.setNome(rs.getString("nome"));
        l.setCpf(rs.getString("cpf"));
        l.setEmail(rs.getString("email"));
        l.setTelefone(rs.getString("telefone"));
        l.setMatricula(rs.getString("matricula"));
        l.setDataNascimento(rs.getDate("data_nascimento"));
        l.setAtivo(rs.getBoolean("ativo"));
        l.setDataCadastro(rs.getDate("inscrito_em"));
        return l;
    }

    private static Date toSqlDate(java.util.Date d) {
        return d != null ? new Date(d.getTime()) : null;
    }

    private static void rollback(Connection c) {
        if (c != null) {
            try {
                c.rollback();
            } catch (SQLException ignored) {
                // nada a fazer
            }
        }
    }

    private static void fechar(Connection c) {
        if (c != null) {
            try {
                c.close();
            } catch (SQLException ignored) {
                // nada a fazer
            }
        }
    }
}
