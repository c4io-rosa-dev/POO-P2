package br.com.biblioteca.ui;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.ui.panel.CopiaPanel;
import br.com.biblioteca.ui.panel.CrudPanel;
import br.com.biblioteca.ui.panel.EmprestimoPanel;
import br.com.biblioteca.ui.panel.FuncionarioPanel;
import br.com.biblioteca.ui.panel.LeitorPanel;
import br.com.biblioteca.ui.panel.ObraPanel;
import br.com.biblioteca.ui.panel.ReservaPanel;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Component;

/**
 * Janela principal: um JTabbedPane com uma aba por entidade.
 *
 * Cada aba é recarregada quando selecionada (dados sempre frescos vindos do
 * banco), e a primeira é carregada na abertura.
 */
public class MainFrame extends JFrame {

    public MainFrame(DAOFactory fac) {
        super("Biblioteca — Sistema de Controle");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Leitores", new LeitorPanel(fac));
        tabs.addTab("Funcionários", new FuncionarioPanel(fac));
        tabs.addTab("Obras", new ObraPanel(fac));
        tabs.addTab("Cópias", new CopiaPanel(fac));
        tabs.addTab("Empréstimos", new EmprestimoPanel(fac));
        tabs.addTab("Reservas", new ReservaPanel(fac));

        // Recarrega a aba ao selecioná-la.
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c instanceof CrudPanel) {
                ((CrudPanel<?>) c).recarregar();
            }
        });

        add(tabs);

        // Carga inicial da primeira aba.
        ((CrudPanel<?>) tabs.getComponentAt(0)).recarregar();
    }
}
