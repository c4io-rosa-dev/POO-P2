package br.com.biblioteca.ui.panel;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.dao.LeitorDAO;
import br.com.biblioteca.models.Leitor;
import br.com.biblioteca.ui.dialog.LeitorFormDialog;

import javax.swing.SwingUtilities;
import java.util.List;

public class LeitorPanel extends CrudPanel<Leitor> {

    private final LeitorDAO dao;

    public LeitorPanel(DAOFactory fac) {
        this.dao = fac.getLeitorDAO();
    }

    @Override
    protected String[] colunas() {
        return new String[]{"ID", "Nome", "CPF", "Matrícula", "E-mail", "Telefone", "Ativo"};
    }

    @Override
    protected Object[] linha(Leitor l) {
        return new Object[]{l.getId(), l.getNome(), l.getCpf(), l.getMatricula(),
                l.getEmail(), l.getTelefone(), l.isAtivo() ? "Sim" : "Não"};
    }

    @Override
    protected List<Leitor> carregar() {
        return dao.listar();
    }

    @Override
    protected void abrirFormulario(Leitor existente) {
        new LeitorFormDialog(SwingUtilities.getWindowAncestor(this), dao, existente).setVisible(true);
    }

    @Override
    protected void excluir(Leitor l) {
        dao.deletar(l.getId());
    }
}
