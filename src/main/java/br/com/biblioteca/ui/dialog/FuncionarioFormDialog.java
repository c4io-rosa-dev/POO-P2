package br.com.biblioteca.ui.dialog;

import br.com.biblioteca.dao.FuncionarioDAO;
import br.com.biblioteca.models.Funcionario;

import javax.swing.JTextField;
import java.awt.Window;

public class FuncionarioFormDialog extends FormDialog {

    private final FuncionarioDAO dao;
    private final Funcionario funcionario;
    private final boolean novo;

    private final JTextField tfNome = new JTextField(20);
    private final JTextField tfCpf = new JTextField(20);
    private final JTextField tfMatricula = new JTextField(20);
    private final JTextField tfEmail = new JTextField(20);
    private final JTextField tfTelefone = new JTextField(20);
    private final JTextField tfNascimento = new JTextField(20);
    private final JTextField tfCargo = new JTextField(20);
    private final JTextField tfSalario = new JTextField(20);
    private final JTextField tfSenha = new JTextField(20);

    public FuncionarioFormDialog(Window owner, FuncionarioDAO dao, Funcionario existente) {
        super(owner, existente == null ? "Novo Funcionário" : "Editar Funcionário");
        this.dao = dao;
        this.novo = existente == null;
        this.funcionario = novo ? new Funcionario() : existente;

        addLinha("Nome:", tfNome);
        addLinha("CPF (11 dígitos):", tfCpf);
        addLinha("Matrícula:", tfMatricula);
        addLinha("E-mail:", tfEmail);
        addLinha("Telefone:", tfTelefone);
        addLinha("Nascimento (dd/MM/yyyy):", tfNascimento);
        addLinha("Cargo:", tfCargo);
        addLinha("Salário:", tfSalario);
        addLinha("Senha:", tfSenha);

        if (!novo) {
            tfNome.setText(funcionario.getNome());
            tfCpf.setText(funcionario.getCpf());
            tfMatricula.setText(funcionario.getMatricula());
            tfEmail.setText(funcionario.getEmail());
            tfTelefone.setText(funcionario.getTelefone());
            tfNascimento.setText(fmt(funcionario.getDataNascimento()));
            tfCargo.setText(funcionario.getCargo());
            tfSalario.setText(String.valueOf(funcionario.getSalario()));
            tfSenha.setText(funcionario.getSenha());
        }

        finalizar();
    }

    @Override
    protected boolean onSalvar() {
        funcionario.setNome(obrigatorio(tfNome.getText(), "Nome"));
        funcionario.setCpf(obrigatorio(tfCpf.getText(), "CPF"));
        funcionario.setMatricula(obrigatorio(tfMatricula.getText(), "Matrícula"));
        funcionario.setEmail(tfEmail.getText().trim());
        funcionario.setTelefone(tfTelefone.getText().trim());
        funcionario.setDataNascimento(parseData(tfNascimento.getText(), "Nascimento"));
        funcionario.setCargo(obrigatorio(tfCargo.getText(), "Cargo"));
        funcionario.setSalario(parseDouble(tfSalario.getText(), "Salário"));
        funcionario.setSenha(obrigatorio(tfSenha.getText(), "Senha"));

        if (novo) {
            dao.salvar(funcionario);
        } else {
            dao.atualizar(funcionario);
        }
        return true;
    }
}
