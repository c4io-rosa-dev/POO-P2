package br.com.biblioteca.ui.dialog;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.exception.BibliotecaException;
import br.com.biblioteca.models.Copia;
import br.com.biblioteca.models.Emprestimo;
import br.com.biblioteca.models.Funcionario;
import br.com.biblioteca.models.Leitor;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Window;
import java.util.Date;

public class EmprestimoFormDialog extends FormDialog {

    private static final String[] STATUS = {"ABERTO", "DEVOLVIDO", "ATRASADO"};

    private final DAOFactory fac;
    private final Emprestimo emprestimo;
    private final boolean novo;

    private final JComboBox<Leitor> cbLeitor = new JComboBox<>();
    private final JComboBox<Copia> cbCopia = new JComboBox<>();
    private final JComboBox<Funcionario> cbFuncionario = new JComboBox<>();
    private final JTextField tfDataEmp = new JTextField(20);
    private final JTextField tfPrevista = new JTextField(20);
    private final JTextField tfDevolucao = new JTextField(20);
    private final JComboBox<String> cbStatus = new JComboBox<>(STATUS);

    public EmprestimoFormDialog(Window owner, DAOFactory fac, Emprestimo existente) {
        super(owner, existente == null ? "Novo Empréstimo" : "Editar Empréstimo");
        this.fac = fac;
        this.novo = existente == null;
        this.emprestimo = novo ? new Emprestimo() : existente;

        for (Leitor l : fac.getLeitorDAO().listar()) {
            cbLeitor.addItem(l);
        }
        for (Copia c : fac.getCopiaDAO().listar()) {
            cbCopia.addItem(c);
        }
        for (Funcionario f : fac.getFuncionarioDAO().listar()) {
            cbFuncionario.addItem(f);
        }

        addLinha("Leitor:", cbLeitor);
        addLinha("Cópia:", cbCopia);
        addLinha("Funcionário:", cbFuncionario);
        addLinha("Data empréstimo (dd/MM/yyyy):", tfDataEmp);
        addLinha("Prevista devolução (dd/MM/yyyy):", tfPrevista);
        addLinha("Data devolução (dd/MM/yyyy):", tfDevolucao);
        addLinha("Status:", cbStatus);

        if (!novo) {
            if (emprestimo.getLeitorId() != null) {
                selecionarPorId(cbLeitor, emprestimo.getLeitorId().getId(), Leitor::getId);
            }
            if (emprestimo.getCopiaId() != null) {
                selecionarPorId(cbCopia, emprestimo.getCopiaId().getId(), Copia::getId);
            }
            if (emprestimo.getFuncionarioId() != null) {
                selecionarPorId(cbFuncionario, emprestimo.getFuncionarioId().getId(), Funcionario::getId);
            }
            tfDataEmp.setText(fmt(emprestimo.getDataEmprestimo()));
            tfPrevista.setText(fmt(emprestimo.getDataPrevistaDevoulcao()));
            tfDevolucao.setText(fmt(emprestimo.getDataDevolucao()));
            cbStatus.setSelectedItem(emprestimo.getStatus());
        }

        finalizar();
    }

    @Override
    protected boolean onSalvar() {
        Leitor leitor = (Leitor) cbLeitor.getSelectedItem();
        Copia copia = (Copia) cbCopia.getSelectedItem();
        Funcionario func = (Funcionario) cbFuncionario.getSelectedItem();
        if (leitor == null || copia == null || func == null) {
            throw new BibliotecaException("Leitor, cópia e funcionário são obrigatórios "
                    + "(cadastre-os antes de registrar um empréstimo).");
        }

        Date prevista = parseData(tfPrevista.getText(), "Prevista devolução");
        if (prevista == null) {
            throw new BibliotecaException("A data prevista de devolução é obrigatória.");
        }
        Date devolucao = parseData(tfDevolucao.getText(), "Data devolução");
        String status = (String) cbStatus.getSelectedItem();

        // Coerência exigida pelo banco: DEVOLVIDO <=> tem data de devolução.
        if ("DEVOLVIDO".equals(status) && devolucao == null) {
            throw new BibliotecaException("Status DEVOLVIDO exige a data de devolução.");
        }
        if (!"DEVOLVIDO".equals(status) && devolucao != null) {
            throw new BibliotecaException("Só preencha a data de devolução quando o status for DEVOLVIDO.");
        }

        emprestimo.setLeitorId(leitor);
        emprestimo.setCopiaId(copia);
        emprestimo.setFuncionarioId(func);
        emprestimo.setDataEmprestimo(parseData(tfDataEmp.getText(), "Data empréstimo"));
        emprestimo.setDataPrevistaDevoulcao(prevista);
        emprestimo.setDataDevolucao(devolucao);
        emprestimo.setStatus(status);

        if (novo) {
            fac.getEmprestimoDAO().salvar(emprestimo);
        } else {
            fac.getEmprestimoDAO().atualizar(emprestimo);
        }
        return true;
    }
}
