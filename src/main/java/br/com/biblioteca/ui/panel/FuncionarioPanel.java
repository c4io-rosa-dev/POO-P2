package br.com.biblioteca.ui.panel;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.dao.FuncionarioDAO;
import br.com.biblioteca.models.Funcionario;
import br.com.biblioteca.ui.dialog.FuncionarioFormDialog;

import javax.swing.SwingUtilities;
import java.util.List;

public class FuncionarioPanel extends CrudPanel<Funcionario> {

    private final FuncionarioDAO dao;

    public FuncionarioPanel(DAOFactory fac) {
        this.dao = fac.getFuncionarioDAO();
    }

    @Override
    protected String[] colunas() {
        return new String[]{"ID", "Nome", "CPF", "Matrícula", "Cargo", "Salário", "Admissão"};
    }

    @Override
    protected Object[] linha(Funcionario f) {
        return new Object[]{f.getId(), f.getNome(), f.getCpf(), f.getMatricula(),
                f.getCargo(), f.getSalario(), fmt(f.getDataAdmissao())};
    }

    @Override
    protected List<Funcionario> carregar() {
        return dao.listar();
    }

    @Override
    protected void abrirFormulario(Funcionario existente) {
        new FuncionarioFormDialog(SwingUtilities.getWindowAncestor(this), dao, existente).setVisible(true);
    }

    @Override
    protected void excluir(Funcionario f) {
        dao.deletar(f.getId());
    }
}
