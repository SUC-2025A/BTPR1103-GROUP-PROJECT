package pawhome;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );

            UIManager.put("Label.foreground", Color.BLACK);
            UIManager.put("Panel.foreground", Color.BLACK);
            UIManager.put("TextField.foreground", Color.BLACK);
            UIManager.put("TextArea.foreground", Color.BLACK);
            UIManager.put("PasswordField.foreground", Color.BLACK);
            UIManager.put("ComboBox.foreground", Color.BLACK);
            UIManager.put("Table.foreground", Color.BLACK);
            UIManager.put("TableHeader.foreground", Color.BLACK);
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("OptionPane.messageForeground", Color.BLACK);

            Database.initialize();

            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Style {
    public static final Color GREEN = new Color(46, 125, 105);
    public static final Color LIGHT_GREEN = new Color(190, 225, 215);
    public static final Color LIGHT = new Color(241, 248, 246);
    public static final Color DARK = new Color(39, 56, 52);
    public static final Color BORDER = new Color(190, 205, 200);

    public static JButton button(String text) {
        JButton button = new JButton(text);

        button.setBackground(LIGHT_GREEN);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );
        button.setBorder(
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        );

        return button;
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);

        label.setFont(
            new Font("SansSerif", Font.BOLD, 25)
        );
        label.setForeground(Color.BLACK);

        return label;
    }

    public static JPanel page() {
        JPanel panel = new JPanel();

        panel.setBackground(LIGHT);
        panel.setForeground(Color.BLACK);
        panel.setBorder(
            BorderFactory.createEmptyBorder(22, 22, 22, 22)
        );

        return panel;
    }

    public static GridBagConstraints constraints(int x, int y) {
        GridBagConstraints constraints =
            new GridBagConstraints();

        constraints.gridx = x;
        constraints.gridy = y;
        constraints.insets = new Insets(7, 7, 7, 7);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        if (x == 1) {
            constraints.weightx = 1;
        }

        return constraints;
    }

    public static void field(JTextField field) {
        field.setFont(
            new Font("SansSerif", Font.PLAIN, 14)
        );
        field.setForeground(Color.BLACK);
        field.setBackground(Color.WHITE);
        field.setCaretColor(Color.BLACK);

        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)
            )
        );
    }

    public static void table(JTable table) {
        table.setRowHeight(28);
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setSelectionForeground(Color.BLACK);
        table.setSelectionBackground(LIGHT_GREEN);
        table.setGridColor(new Color(220, 230, 226));

        table.getTableHeader().setBackground(LIGHT_GREEN);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );

        DefaultTableCellRenderer centerRenderer =
            new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setForeground(Color.BLACK);

        for (
            int column = 0;
            column < table.getColumnCount();
            column++
        ) {
            table.getColumnModel()
                .getColumn(column)
                .setCellRenderer(centerRenderer);
        }
    }

    public static void labelTree(Component component) {
        component.setFont(
            new Font("SansSerif", Font.PLAIN, 13)
        );
        component.setForeground(Color.BLACK);
    }
}