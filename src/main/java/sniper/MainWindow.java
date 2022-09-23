package sniper;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainWindow extends JFrame {

    private final JLabel sniperStatus = createLabel("joining");

    private static JLabel createLabel(String initialText) {
        JLabel label = new JLabel(initialText);
        label.setName("sniperStatus");
        label.setBorder(new LineBorder(Color.BLACK));

        return label;
    }

    public void showsStatus(String statusText) {
        sniperStatus.setText(statusText);
    }

    public MainWindow() {
        super("auction sniper");
        setName("MAIN WINDOW");
        add(sniperStatus);
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}
