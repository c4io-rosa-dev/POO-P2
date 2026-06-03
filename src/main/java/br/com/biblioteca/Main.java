package br.com.biblioteca;

import br.com.biblioteca.dao.DAOFactory;
import br.com.biblioteca.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Look-and-feel nativo do sistema operacional.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // se falhar, segue com o visual padrão do Swing
        }

        DAOFactory fac = new DAOFactory();
        SwingUtilities.invokeLater(() -> new MainFrame(fac).setVisible(true));
    }
}
