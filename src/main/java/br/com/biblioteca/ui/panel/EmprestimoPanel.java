package br.com.biblioteca.ui.panel;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.models.Emprestimo;
import br.com.biblioteca.ui.dialog.EmprestimoFormDialog;

import javax.swing.SwingUtilities;
import java.util.List;

public class EmprestimoPanel extends CrudPanel<Emprestimo> {

    private final DAOFactory fac;

    public EmprestimoPanel(DAOFactory fac) {
        this.fac = fac;
    }

    @Override
    protected String[] colunas() {
        return new String[]{"ID", "Leitor", "Cópia", "Funcionário",
                "Empréstimo", "Prev. Devolução", "Devolução", "Status"};
    }

    @Override
    protected Object[] linha(Emprestimo e) {
        String leitor = (e.getLeitorId() != null) ? e.getLeitorId().getNome() : "";
        String copia = (e.getCopiaId() != null) ? e.getCopiaId().getCodigoTombo() : "";
        String func = (e.getFuncionarioId() != null) ? e.getFuncionarioId().getNome() : "";
        return new Object[]{e.getId(), leitor, copia, func,
                fmt(e.getDataEmprestimo()), fmt(e.getDataPrevistaDevoulcao()),
                fmt(e.getDataDevolucao()), e.getStatus()};
    }

    @Override
    protected List<Emprestimo> carregar() {
        return fac.getEmprestimoDAO().listar();
    }

    @Override
    protected void abrirFormulario(Emprestimo existente) {
        new EmprestimoFormDialog(SwingUtilities.getWindowAncestor(this), fac, existente).setVisible(true);
    }

    @Override
    protected void excluir(Emprestimo e) {
        fac.getEmprestimoDAO().deletar(e.getId());
    }
}
