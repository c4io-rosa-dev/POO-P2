package br.com.biblioteca.ui.dialog;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.exception.BibliotecaException;
import br.com.biblioteca.models.Leitor;
import br.com.biblioteca.models.Obra;
import br.com.biblioteca.models.Reserva;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Window;
import java.util.Date;

public class ReservaFormDialog extends FormDialog {

    private static final String[] STATUS = {"ATIVA", "ATENDIDA", "CANCELADA", "EXPIRADA"};

    private final DAOFactory fac;
    private final Reserva reserva;
    private final boolean novo;

    private final JComboBox<Leitor> cbLeitor = new JComboBox<>();
    private final JComboBox<Obra> cbObra = new JComboBox<>();
    private final JTextField tfReserva = new JTextField(20);
    private final JTextField tfExpiracao = new JTextField(20);
    private final JComboBox<String> cbStatus = new JComboBox<>(STATUS);

    public ReservaFormDialog(Window owner, DAOFactory fac, Reserva existente) {
        super(owner, existente == null ? "Nova Reserva" : "Editar Reserva");
        this.fac = fac;
        this.novo = existente == null;
        this.reserva = novo ? new Reserva() : existente;

        for (Leitor l : fac.getLeitorDAO().listar()) {
            cbLeitor.addItem(l);
        }
        for (Obra o : fac.getObraDAO().listar()) {
            cbObra.addItem(o);
        }

        addLinha("Leitor:", cbLeitor);
        addLinha("Obra:", cbObra);
        addLinha("Data reserva (dd/MM/yyyy):", tfReserva);
        addLinha("Expiração (dd/MM/yyyy):", tfExpiracao);
        addLinha("Status:", cbStatus);

        if (!novo) {
            if (reserva.getLeitorId() != null) {
                selecionarPorId(cbLeitor, reserva.getLeitorId().getId(), Leitor::getId);
            }
            if (reserva.getObraId() != null) {
                selecionarPorId(cbObra, reserva.getObraId().getId(), Obra::getId);
            }
            tfReserva.setText(fmt(reserva.getDataReserva()));
            tfExpiracao.setText(fmt(reserva.getDataExpiracao()));
            cbStatus.setSelectedItem(reserva.getStatus());
        }

        finalizar();
    }

    @Override
    protected boolean onSalvar() {
        Leitor leitor = (Leitor) cbLeitor.getSelectedItem();
        Obra obra = (Obra) cbObra.getSelectedItem();
        if (leitor == null || obra == null) {
            throw new BibliotecaException("Leitor e obra são obrigatórios "
                    + "(cadastre-os antes de registrar uma reserva).");
        }

        Date expiracao = parseData(tfExpiracao.getText(), "Expiração");
        if (expiracao == null) {
            throw new BibliotecaException("A data de expiração é obrigatória.");
        }

        reserva.setLeitorId(leitor);
        reserva.setObraId(obra);
        reserva.setDataReserva(parseData(tfReserva.getText(), "Data reserva"));
        reserva.setDataExpiracao(expiracao);
        reserva.setStatus((String) cbStatus.getSelectedItem());

        if (novo) {
            fac.getReservaDAO().salvar(reserva);
        } else {
            fac.getReservaDAO().atualizar(reserva);
        }
        return true;
    }
}
