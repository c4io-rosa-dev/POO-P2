package br.com.biblioteca.ui.panel;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.models.Reserva;
import br.com.biblioteca.ui.dialog.ReservaFormDialog;

import javax.swing.SwingUtilities;
import java.util.List;

public class ReservaPanel extends CrudPanel<Reserva> {

    private final DAOFactory fac;

    public ReservaPanel(DAOFactory fac) {
        this.fac = fac;
    }

    @Override
    protected String[] colunas() {
        return new String[]{"ID", "Leitor", "Obra", "Reserva", "Expiração", "Status"};
    }

    @Override
    protected Object[] linha(Reserva r) {
        String leitor = (r.getLeitorId() != null) ? r.getLeitorId().getNome() : "";
        String obra = (r.getObraId() != null) ? r.getObraId().getTitulo() : "";
        return new Object[]{r.getId(), leitor, obra,
                fmt(r.getDataReserva()), fmt(r.getDataExpiracao()), r.getStatus()};
    }

    @Override
    protected List<Reserva> carregar() {
        return fac.getReservaDAO().listar();
    }

    @Override
    protected void abrirFormulario(Reserva existente) {
        new ReservaFormDialog(SwingUtilities.getWindowAncestor(this), fac, existente).setVisible(true);
    }

    @Override
    protected void excluir(Reserva r) {
        fac.getReservaDAO().deletar(r.getId());
    }
}
