package br.com.biblioteca.dao.impl;

import br.com.biblioteca.dao.FuncionarioDAO;
import br.com.biblioteca.exception.BibliotecaException;
import br.com.biblioteca.models.Funcionario;
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
 * Implementação JDBC do FuncionarioDAO.
 *
 * Mesma herança Class Table Inheritance do leitor: dados comuns em "pessoa",
 * específicos em "funcionario". salvar/atualizar usam transação nas duas tabelas.
 */
public class FuncionarioDAOImpl implements FuncionarioDAO {

    @Override
    public void salvar(Funcionario f) {
        String sqlPessoa = "INSERT INTO pessoa (nome, cpf, email, telefone, matricula, data_nascimento) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlFunc = "INSERT INTO funcionario (id, cargo, salario, admitido_em, senha) VALUES (?, ?, ?, ?, ?)";

        Connection c = null;
        try {
            c = ConnectionFactory.getConnection();
            c.setAutoCommit(false);

            int idGerado;
            try (PreparedStatement ps = c.prepareStatement(sqlPessoa, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, f.getNome());
                ps.setString(2, f.getCpf());
                ps.setString(3, f.getEmail());
                ps.setString(4, f.getTelefone());
                ps.setString(5, f.getMatricula());
                ps.setDate(6, toSqlDate(f.getDataNascimento()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    idGerado = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(sqlFunc)) {
                ps.setInt(1, idGerado);
                ps.setString(2, f.getCargo());
                ps.setDouble(3, f.getSalario());
                ps.setDate(4, f.getDataAdmissao() != null
                        ? toSqlDate(f.getDataAdmissao())
                        : new Date(System.currentTimeMillis()));
                ps.setString(5, f.getSenha());
                ps.executeUpdate();
            }

            c.commit();
            f.setId(idGerado);
        } catch (SQLException e) {
            rollback(c);
            throw new BibliotecaException("Erro ao salvar funcionário: " + e.getMessage(), e);
        } finally {
            fechar(c);
        }
    }

    @Override
    public void atualizar(Funcionario f) {
        String sqlPessoa = "UPDATE pessoa SET nome=?, cpf=?, email=?, telefone=?, matricula=?, data_nascimento=? "
                + "WHERE id=?";
        String sqlFunc = "UPDATE funcionario SET cargo=?, salario=?, admitido_em=?, senha=? WHERE id=?";

        Connection c = null;
        try {
            c = ConnectionFactory.getConnection();
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(sqlPessoa)) {
                ps.setString(1, f.getNome());
                ps.setString(2, f.getCpf());
                ps.setString(3, f.getEmail());
                ps.setString(4, f.getTelefone());
                ps.setString(5, f.getMatricula());
                ps.setDate(6, toSqlDate(f.getDataNascimento()));
                ps.setInt(7, f.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(sqlFunc)) {
                ps.setString(1, f.getCargo());
                ps.setDouble(2, f.getSalario());
                ps.setDate(3, toSqlDate(f.getDataAdmissao()));
                ps.setString(4, f.getSenha());
                ps.setInt(5, f.getId());
                ps.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            rollback(c);
            throw new BibliotecaException("Erro ao atualizar funcionário: " + e.getMessage(), e);
        } finally {
            fechar(c);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM pessoa WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao deletar funcionário: " + e.getMessage(), e);
        }
    }

    @Override
    public Funcionario buscar(int id) {
        String sql = "SELECT p.id, p.nome, p.cpf, p.email, p.telefone, p.matricula, p.data_nascimento, "
                + "f.cargo, f.salario, f.admitido_em, f.senha "
                + "FROM funcionario f JOIN pessoa p ON p.id = f.id WHERE f.id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao buscar funcionário: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Funcionario> listar() {
        String sql = "SELECT p.id, p.nome, p.cpf, p.email, p.telefone, p.matricula, p.data_nascimento, "
                + "f.cargo, f.salario, f.admitido_em, f.senha "
                + "FROM funcionario f JOIN pessoa p ON p.id = f.id ORDER BY p.nome";
        List<Funcionario> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new BibliotecaException("Erro ao listar funcionários: " + e.getMessage(), e);
        }
    }

    private Funcionario montar(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setNome(rs.getString("nome"));
        f.setCpf(rs.getString("cpf"));
        f.setEmail(rs.getString("email"));
        f.setTelefone(rs.getString("telefone"));
        f.setMatricula(rs.getString("matricula"));
        f.setDataNascimento(rs.getDate("data_nascimento"));
        f.setCargo(rs.getString("cargo"));
        f.setSalario(rs.getDouble("salario"));
        f.setDataAdmissao(rs.getDate("admitido_em"));
        f.setSenha(rs.getString("senha"));
        return f;
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
