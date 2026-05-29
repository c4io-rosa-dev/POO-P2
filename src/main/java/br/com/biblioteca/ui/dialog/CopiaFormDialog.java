package br.com.biblioteca.ui.dialog;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.exception.BibliotecaException;
import br.com.biblioteca.models.Copia;
import br.com.biblioteca.models.Obra;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Window;
import java.util.List;

public class CopiaFormDialog extends FormDialog {

    private static final String[] ESTADOS =
            {"DISPONIVEL", "EMPRESTADA", "RESERVADA", "DANIFICADA", "PERDIDA"};

    private final DAOFactory fac;
    private final Copia copia;
    private final boolean novo;

    private final JComboBox<Obra> cbObra = new JComboBox<>();
    private final JTextField tfTombo = new JTextField(20);
    private final JComboBox<String> cbEstado = new JComboBox<>(ESTADOS);
    private final JTextField tfAdquirida = new JTextField(20);
    private final JTextField tfObservacoes = new JTextField(20);

    public CopiaFormDialog(Window owner, DAOFactory fac, Copia existente) {
        super(owner, existente == null ? "Nova Cópia" : "Editar Cópia");
        this.fac = fac;
        this.novo = existente == null;
        this.copia = novo ? new Copia() : existente;

        for (Obra o : fac.getObraDAO().listar()) {
            cbObra.addItem(o);
        }

        addLinha("Obra:", cbObra);
        addLinha("Código de tombo:", tfTombo);
        addLinha("Estado:", cbEstado);
        addLinha("Adquirida em (dd/MM/yyyy):", tfAdquirida);
        addLinha("Observações:", tfObservacoes);

        if (!novo) {
            if (copia.getObra() != null) {
                selecionarPorId(cbObra, copia.getObra().getId(), Obra::getId);
            }
            tfTombo.setText(copia.getCodigoTombo());
            cbEstado.setSelectedItem(copia.getEstado());
            tfAdquirida.setText(fmt(copia.getDataAdquirida()));
            tfObservacoes.setText(copia.getObservacoes());
        }

        finalizar();
    }

    @Override
    protected boolean onSalvar() {
        Obra obra = (Obra) cbObra.getSelectedItem();
        if (obra == null) {
            throw new BibliotecaException("Cadastre uma obra antes de criar uma cópia.");
        }
        copia.setObra(obra);
        copia.setCodigoTombo(obrigatorio(tfTombo.getText(), "Código de tombo"));
        copia.setEstado((String) cbEstado.getSelectedItem());
        copia.setDataAdquirida(parseData(tfAdquirida.getText(), "Adquirida em"));
        copia.setObservacoes(tfObservacoes.getText().trim());

        if (novo) {
            fac.getCopiaDAO().salvar(copia);
        } else {
            fac.getCopiaDAO().atualizar(copia);
        }
        return true;
    }
}
