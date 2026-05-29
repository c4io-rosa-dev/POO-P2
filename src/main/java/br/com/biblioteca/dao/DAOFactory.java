package br.com.biblioteca.dao;

import br.com.biblioteca.dao.impl.CopiaDAOImpl;
import br.com.biblioteca.dao.impl.EmprestimoDAOImpl;
import br.com.biblioteca.dao.impl.FuncionarioDAOImpl;
import br.com.biblioteca.dao.impl.LeitorDAOImpl;
import br.com.biblioteca.dao.impl.ObraDAOImpl;
import br.com.biblioteca.dao.impl.ReservaDAOImpl;

/**
 * Fábrica central de DAOs.
 *
 * É o único ponto da aplicação que sabe QUAL implementação concreta de cada DAO
 * usar. A UI depende apenas das interfaces e pede os DAOs aqui — assim, trocar a
 * implementação (ex.: por um stub de teste) seria mudança em um lugar só.
 */
public class DAOFactory {

    public LeitorDAO getLeitorDAO() {
        return new LeitorDAOImpl();
    }

    public FuncionarioDAO getFuncionarioDAO() {
        return new FuncionarioDAOImpl();
    }

    public ObraDAO getObraDAO() {
        return new ObraDAOImpl();
    }

    public CopiaDAO getCopiaDAO() {
        return new CopiaDAOImpl();
    }

    public EmprestimoDAO getEmprestimoDAO() {
        return new EmprestimoDAOImpl();
    }

    public ReservaDAO getReservaDAO() {
        return new ReservaDAOImpl();
    }
}
