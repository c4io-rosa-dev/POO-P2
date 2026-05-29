package br.com.biblioteca.ui.dialog;

import br.com.biblioteca.exception.BibliotecaException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.function.ToIntFunction;

/**
 * Base dos formulários de cadastro/edição (JDialog modal).
 *
 * Monta o layout (rótulo + campo por linha) e os botões Salvar/Cancelar, e
 * centraliza os utilitários comuns: parse/format de datas e seleção de combo
 * por id. A subclasse só monta os campos e implementa {@link #onSalvar()}.
 *
 * O controle de erros fica aqui (camada UI): ao salvar, qualquer
 * BibliotecaException — de validação ou vinda do DAO — é capturada e exibida num
 * JOptionPane, mantendo o formulário aberto para correção.
 */
public abstract class FormDialog extends JDialog {

    protected static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");

    private final JPanel campos = new JPanel(new GridBagLayout());
    private final GridBagConstraints gbc = new GridBagConstraints();
    private int linhaAtual = 0;

    protected FormDialog(Window owner, String titulo) {
        super(owner, titulo, ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(8, 8));

        campos.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        gbc.insets = new Insets(4, 4, 4, 4);
        add(campos, BorderLayout.CENTER);

        JButton salvar = new JButton("Salvar");
        JButton cancelar = new JButton("Cancelar");
        JPanel sul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sul.add(salvar);
        sul.add(cancelar);
        add(sul, BorderLayout.SOUTH);

        salvar.addActionListener(e -> {
            try {
                if (onSalvar()) {
                    dispose();
                }
            } catch (BibliotecaException ex) {
                erro(ex.getMessage());
            }
        });
        cancelar.addActionListener(e -> dispose());
    }

    /** Adiciona uma linha "rótulo: componente" ao formulário. */
    protected void addLinha(String rotulo, JComponent componente) {
        gbc.gridx = 0;
        gbc.gridy = linhaAtual;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        campos.add(new JLabel(rotulo), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        campos.add(componente, gbc);
        linhaAtual++;
    }

    /** Finaliza a montagem: ajusta tamanho e centraliza sobre o owner. */
    protected void finalizar() {
        pack();
        setLocationRelativeTo(getOwner());
    }

    /**
     * Valida e persiste. Retorne true para fechar o diálogo; lance
     * {@link BibliotecaException} para mostrar erro e manter o formulário aberto.
     */
    protected abstract boolean onSalvar();

    protected void erro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // ---- utilitários compartilhados ----

    /** Exige que um texto não seja vazio. */
    protected String obrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BibliotecaException("O campo '" + campo + "' é obrigatório.");
        }
        return valor.trim();
    }

    /** Converte texto dd/MM/yyyy em Date; vazio vira null; formato inválido lança erro. */
    protected static java.util.Date parseData(String s, String campo) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            FMT.setLenient(false);
            return FMT.parse(s.trim());
        } catch (ParseException e) {
            throw new BibliotecaException("Data inválida em '" + campo + "'. Use o formato dd/MM/yyyy.");
        }
    }

    protected static String fmt(java.util.Date d) {
        return d == null ? "" : FMT.format(d);
    }

    protected static int parseInt(String s, String campo) {
        if (s == null || s.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new BibliotecaException("O campo '" + campo + "' deve ser um número inteiro.");
        }
    }

    protected static double parseDouble(String s, String campo) {
        if (s == null || s.trim().isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(s.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new BibliotecaException("O campo '" + campo + "' deve ser um número.");
        }
    }

    /** Seleciona no combo o item cujo id casa com o informado. */
    protected static <X> void selecionarPorId(JComboBox<X> combo, int id, ToIntFunction<X> getId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (getId.applyAsInt(combo.getItemAt(i)) == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }
}
