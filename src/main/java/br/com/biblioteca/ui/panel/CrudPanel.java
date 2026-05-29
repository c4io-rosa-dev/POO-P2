package br.com.biblioteca.ui.panel;

import br.com.biblioteca.exception.BibliotecaException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel CRUD genérico (padrão Template Method).
 *
 * Concentra o que é comum a todas as abas — JTable, botões Novo/Editar/Excluir/
 * Atualizar e o tratamento de erros via JOptionPane — e delega à subclasse só o
 * que varia por entidade (colunas, montagem da linha, carga, formulário e
 * exclusão). Demonstra herança, generics e polimorfismo na própria camada de UI.
 *
 * Toda chamada ao DAO é feita aqui dentro de try/catch: é o ponto onde as
 * exceções (BibliotecaException) lançadas pela camada de dados são tratadas,
 * conforme o enunciado pede (controle de erros próximo da UI).
 */
public abstract class CrudPanel<T> extends JPanel {

    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");

    private final JTable tabela;
    private final DefaultTableModel modelo;
    private List<T> dados = new ArrayList<>();

    protected CrudPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bNovo = new JButton("Novo");
        JButton bEditar = new JButton("Editar");
        JButton bExcluir = new JButton("Excluir");
        JButton bAtualizar = new JButton("Atualizar");
        barra.add(bNovo);
        barra.add(bEditar);
        barra.add(bExcluir);
        barra.add(bAtualizar);
        add(barra, BorderLayout.NORTH);

        bNovo.addActionListener(e -> {
            try {
                abrirFormulario(null);
            } catch (BibliotecaException ex) {
                erro(ex.getMessage());
            }
            recarregar();
        });
        bEditar.addActionListener(e -> {
            T sel = selecionado();
            if (sel == null) {
                aviso("Selecione um item para editar.");
                return;
            }
            try {
                abrirFormulario(sel);
            } catch (BibliotecaException ex) {
                erro(ex.getMessage());
            }
            recarregar();
        });
        bExcluir.addActionListener(e -> {
            T sel = selecionado();
            if (sel == null) {
                aviso("Selecione um item para excluir.");
                return;
            }
            int op = JOptionPane.showConfirmDialog(this, "Confirma a exclusão do item selecionado?",
                    "Excluir", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                try {
                    excluir(sel);
                } catch (BibliotecaException ex) {
                    erro(ex.getMessage());
                }
                recarregar();
            }
        });
        bAtualizar.addActionListener(e -> recarregar());
    }

    /** Recarrega a tabela a partir do DAO. Trata erros aqui (camada UI). */
    public void recarregar() {
        try {
            dados = carregar();
            modelo.setColumnIdentifiers(colunas());
            modelo.setRowCount(0);
            for (T item : dados) {
                modelo.addRow(linha(item));
            }
        } catch (BibliotecaException ex) {
            erro(ex.getMessage());
        }
    }

    /** Item selecionado na tabela, ou null se nada selecionado. */
    protected T selecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return dados.get(tabela.convertRowIndexToModel(row));
    }

    protected void erro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    protected void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    /** Formata uma data para exibição (dd/MM/yyyy). */
    protected String fmt(java.util.Date d) {
        return d == null ? "" : FMT.format(d);
    }

    // ---- Template methods: cada aba implementa o que é específico dela ----

    protected abstract String[] colunas();

    protected abstract Object[] linha(T item);

    protected abstract List<T> carregar();

    /** Abre o formulário modal. {@code existente == null} significa "novo". */
    protected abstract void abrirFormulario(T existente);

    protected abstract void excluir(T item);
}
