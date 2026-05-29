package br.com.biblioteca.ui.dialog;

import br.com.biblioteca.dao.LeitorDAO;
import br.com.biblioteca.models.Leitor;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.Window;

public class LeitorFormDialog extends FormDialog {

    private final LeitorDAO dao;
    private final Leitor leitor;
    private final boolean novo;

    private final JTextField tfNome = new JTextField(20);
    private final JTextField tfCpf = new JTextField(20);
    private final JTextField tfMatricula = new JTextField(20);
    private final JTextField tfEmail = new JTextField(20);
    private final JTextField tfTelefone = new JTextField(20);
    private final JTextField tfNascimento = new JTextField(20);
    private final JCheckBox cbAtivo = new JCheckBox("Ativo", true);

    public LeitorFormDialog(Window owner, LeitorDAO dao, Leitor existente) {
        super(owner, existente == null ? "Novo Leitor" : "Editar Leitor");
        this.dao = dao;
        this.novo = existente == null;
        this.leitor = novo ? new Leitor() : existente;

        addLinha("Nome:", tfNome);
        addLinha("CPF (11 dígitos):", tfCpf);
        addLinha("Matrícula:", tfMatricula);
        addLinha("E-mail:", tfEmail);
        addLinha("Telefone:", tfTelefone);
        addLinha("Nascimento (dd/MM/yyyy):", tfNascimento);
        addLinha("", cbAtivo);

        if (!novo) {
            tfNome.setText(leitor.getNome());
            tfCpf.setText(leitor.getCpf());
            tfMatricula.setText(leitor.getMatricula());
            tfEmail.setText(leitor.getEmail());
            tfTelefone.setText(leitor.getTelefone());
            tfNascimento.setText(fmt(leitor.getDataNascimento()));
            cbAtivo.setSelected(leitor.isAtivo());
        }

        finalizar();
    }

    @Override
    protected boolean onSalvar() {
        leitor.setNome(obrigatorio(tfNome.getText(), "Nome"));
        leitor.setCpf(obrigatorio(tfCpf.getText(), "CPF"));
        leitor.setMatricula(obrigatorio(tfMatricula.getText(), "Matrícula"));
        leitor.setEmail(tfEmail.getText().trim());
        leitor.setTelefone(tfTelefone.getText().trim());
        leitor.setDataNascimento(parseData(tfNascimento.getText(), "Nascimento"));
        leitor.setAtivo(cbAtivo.isSelected());

        if (novo) {
            dao.salvar(leitor);
        } else {
            dao.atualizar(leitor);
        }
        return true;
    }
}
