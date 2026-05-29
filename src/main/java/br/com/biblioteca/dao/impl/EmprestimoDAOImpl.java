package br.com.biblioteca.dao.impl;

import br.com.biblioteca.dao.EmprestimoDAO;
import br.com.biblioteca.exception.BibliotecaException;
import br.com.biblioteca.models.Copia;
import br.com.biblioteca.models.Emprestimo;
import br.com.biblioteca.models.Funcionario;
import br.com.biblioteca.models.Leitor;
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
 * Implementação JDBC do EmprestimoDAO.
 *
 * ASSOCIAÇÃO ternária: empréstimo liga leitor + cópia + funcionário. Ao gravar
 * usamos os ids dos objetos referenciados; ao ler fazemos JOINs para popular
 * cada referência com os campos necessários para exibição.
 */
public class EmprestimoDAOImpl implements EmprestimoDAO {

    @Override
    public void salvar(Emprestimo e) {
        String sql = "INSERT INTO emprestimo (leitor_id, copia_id, funcionario_id, data_emprestimo, "
                + "data_prevista_devolucao, data_devolucao, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getLeitorId().getId());
            ps.setInt(2, e.getCopiaId().getId());
            ps.setInt(3, e.getFuncionarioId().getId());
            ps.setDate(4, e.getDataEmprestimo() != null
                    ? toSqlDate(e.getDataEmprestimo())
                    : new Date(System.currentTimeMillis()));
            ps.setDate(5, toSqlDate(e.getDataPrevistaDevoulcao()));
            ps.setDate(6, toSqlDate(e.getDataDevolucao()));
            ps.setString(7, e.getStatus() != null ? e.getStatus() : "ABERTO");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao salvar empréstimo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void atualizar(Emprestimo e) {
        String sql = "UPDATE emprestimo SET leitor_id=?, copia_id=?, funcionario_id=?, data_emprestimo=?, "
                + "data_prevista_devolucao=?, data_devolucao=?, status=? WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, e.getLeitorId().getId());
            ps.setInt(2, e.getCopiaId().getId());
            ps.setInt(3, e.getFuncionarioId().getId());
            ps.setDate(4, toSqlDate(e.getDataEmprestimo()));
            ps.setDate(5, toSqlDate(e.getDataPrevistaDevoulcao()));
            ps.setDate(6, toSqlDate(e.getDataDevolucao()));
            ps.setString(7, e.getStatus());
            ps.setInt(8, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao atualizar empréstimo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM emprestimo WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao deletar empréstimo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Emprestimo buscar(int id) {
        String sql = baseSelect() + " WHERE e.id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao buscar empréstimo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Emprestimo> listar() {
        String sql = baseSelect() + " ORDER BY e.data_emprestimo DESC, e.id DESC";
        List<Emprestimo> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
            return lista;
        } catch (SQLException ex) {
            throw new BibliotecaException("Erro ao listar empréstimos: " + ex.getMessage(), ex);
        }
    }

    private String baseSelect() {
        return "SELECT e.id, e.data_emprestimo, e.data_prevista_devolucao, e.data_devolucao, e.status, "
                + "li.id AS leitor_id, pl.nome AS leitor_nome, pl.matricula AS leitor_matricula, "
                + "fu.id AS func_id, pf.nome AS func_nome, pf.matricula AS func_matricula, "
                + "c.id AS copia_id, c.codigo_tombo, c.estado, "
                + "o.id AS obra_id, o.titulo AS obra_titulo "
                + "FROM emprestimo e "
                + "JOIN leitor li ON li.id = e.leitor_id "
                + "JOIN pessoa pl ON pl.id = li.id "
                + "JOIN funcionario fu ON fu.id = e.funcionario_id "
                + "JOIN pessoa pf ON pf.id = fu.id "
                + "JOIN copia c ON c.id = e.copia_id "
                + "JOIN obra o ON o.id = c.obra_id";
    }

    private Emprestimo montar(ResultSet rs) throws SQLException {
        Leitor leitor = new Leitor();
        leitor.setId(rs.getInt("leitor_id"));
        leitor.setNome(rs.getString("leitor_nome"));
        leitor.setMatricula(rs.getString("leitor_matricula"));

        Funcionario func = new Funcionario();
        func.setId(rs.getInt("func_id"));
        func.setNome(rs.getString("func_nome"));
        func.setMatricula(rs.getString("func_matricula"));

        Obra obra = new Obra();
        obra.setId(rs.getInt("obra_id"));
        obra.setTitulo(rs.getString("obra_titulo"));

        Copia copia = new Copia();
        copia.setId(rs.getInt("copia_id"));
        copia.setCodigoTombo(rs.getString("codigo_tombo"));
        copia.setEstado(rs.getString("estado"));
        copia.setObra(obra);

        Emprestimo e = new Emprestimo();
        e.setId(rs.getInt("id"));
        e.setLeitorId(leitor);
        e.setCopiaId(copia);
        e.setFuncionarioId(func);
        e.setDataEmprestimo(rs.getDate("data_emprestimo"));
        e.setDataPrevistaDevoulcao(rs.getDate("data_prevista_devolucao"));
        e.setDataDevolucao(rs.getDate("data_devolucao"));
        e.setStatus(rs.getString("status"));
        return e;
    }

    private static Date toSqlDate(java.util.Date d) {
        return d != null ? new Date(d.getTime()) : null;
    }
}
