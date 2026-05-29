package br.com.biblioteca.ui.panel;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.dao.ObraDAO;
import br.com.biblioteca.models.Obra;
import br.com.biblioteca.ui.dialog.ObraFormDialog;

import javax.swing.SwingUtilities;
import java.util.List;

public class ObraPanel extends CrudPanel<Obra> {

    private final ObraDAO dao;

    public ObraPanel(DAOFactory fac) {
        this.dao = fac.getObraDAO();
    }

    @Override
    protected String[] colunas() {
        return new String[]{"ID", "Título", "Autor", "Editora", "Ano", "ISBN", "Categoria", "Tipo"};
    }

    @Override
    protected Object[] linha(Obra o) {
        return new Object[]{o.getId(), o.getTitulo(), o.getAutor(), o.getEditora(),
                o.getAnoPublicacao(), o.getIsbn(), o.getCategoria(), o.getTipo()};
    }

    @Override
    protected List<Obra> carregar() {
        return dao.listar();
    }

    @Override
    protected void abrirFormulario(Obra existente) {
        new ObraFormDialog(SwingUtilities.getWindowAncestor(this), dao, existente).setVisible(true);
    }

    @Override
    protected void excluir(Obra o) {
        dao.deletar(o.getId());
    }
}
