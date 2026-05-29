package br.com.biblioteca.ui.dialog;

import br.com.biblioteca.dao.ObraDAO;
import br.com.biblioteca.models.Obra;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Window;

public class ObraFormDialog extends FormDialog {

    private static final String[] TIPOS = {"LIVRO", "REVISTA", "PERIODICO"};

    private final ObraDAO dao;
    private final Obra obra;
    private final boolean novo;

    private final JTextField tfTitulo = new JTextField(20);
    private final JTextField tfAutor = new JTextField(20);
    private final JTextField tfEditora = new JTextField(20);
    private final JTextField tfAno = new JTextField(20);
    private final JTextField tfIsbn = new JTextField(20);
    private final JTextField tfCategoria = new JTextField(20);
    private final JComboBox<String> cbTipo = new JComboBox<>(TIPOS);

    public ObraFormDialog(Window owner, ObraDAO dao, Obra existente) {
        super(owner, existente == null ? "Nova Obra" : "Editar Obra");
        this.dao = dao;
        this.novo = existente == null;
        this.obra = novo ? new Obra() : existente;

        addLinha("Título:", tfTitulo);
        addLinha("Autor:", tfAutor);
        addLinha("Editora:", tfEditora);
        addLinha("Ano de publicação:", tfAno);
        addLinha("ISBN:", tfIsbn);
        addLinha("Categoria:", tfCategoria);
        addLinha("Tipo:", cbTipo);

        if (!novo) {
            tfTitulo.setText(obra.getTitulo());
            tfAutor.setText(obra.getAutor());
            tfEditora.setText(obra.getEditora());
            tfAno.setText(obra.getAnoPublicacao() > 0 ? String.valueOf(obra.getAnoPublicacao()) : "");
            tfIsbn.setText(obra.getIsbn());
            tfCategoria.setText(obra.getCategoria());
            cbTipo.setSelectedItem(obra.getTipo());
        }

        finalizar();
    }

    @Override
    protected boolean onSalvar() {
        obra.setTitulo(obrigatorio(tfTitulo.getText(), "Título"));
        obra.setAutor(obrigatorio(tfAutor.getText(), "Autor"));
        obra.setEditora(tfEditora.getText().trim());
        obra.setAnoPublicacao(parseInt(tfAno.getText(), "Ano de publicação"));
        obra.setIsbn(tfIsbn.getText().trim());
        obra.setCategoria(tfCategoria.getText().trim());
        obra.setTipo((String) cbTipo.getSelectedItem());

        if (novo) {
            dao.salvar(obra);
        } else {
            dao.atualizar(obra);
        }
        return true;
    }
}
