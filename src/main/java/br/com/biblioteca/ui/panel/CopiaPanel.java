package br.com.biblioteca.ui.panel;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.models.Copia;
import br.com.biblioteca.ui.dialog.CopiaFormDialog;

import javax.swing.SwingUtilities;
import java.util.List;

public class CopiaPanel extends CrudPanel<Copia> {

    private final DAOFactory fac;

    public CopiaPanel(DAOFactory fac) {
        this.fac = fac;
    }

    @Override
    protected String[] colunas() {
        return new String[]{"ID", "Tombo", "Obra", "Estado", "Adquirida em", "Observações"};
    }

    @Override
    protected Object[] linha(Copia c) {
        String tituloObra = (c.getObra() != null) ? c.getObra().getTitulo() : "";
        return new Object[]{c.getId(), c.getCodigoTombo(), tituloObra, c.getEstado(),
                fmt(c.getDataAdquirida()), c.getObservacoes()};
    }

    @Override
    protected List<Copia> carregar() {
        return fac.getCopiaDAO().listar();
    }

    @Override
    protected void abrirFormulario(Copia existente) {
        new CopiaFormDialog(SwingUtilities.getWindowAncestor(this), fac, existente).setVisible(true);
    }

    @Override
    protected void excluir(Copia c) {
        fac.getCopiaDAO().deletar(c.getId());
    }
}
