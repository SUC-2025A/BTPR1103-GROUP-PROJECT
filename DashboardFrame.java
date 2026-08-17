package pawhome;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DashboardFrame extends JFrame {
    private final User user;

    private final CardLayout cardLayout =
        new CardLayout();

    private final JPanel contentPanel =
        new JPanel(cardLayout);

    public DashboardFrame(User user) {
        this.user = user;

        setTitle("PawHome - Dashboard");
        setSize(1150, 720);
        setMinimumSize(
            new Dimension(980, 620)
        );
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(
            createSidebar(),
            BorderLayout.WEST
        );

        add(
            contentPanel,
            BorderLayout.CENTER
        );

        contentPanel.add(
            new HomePanel(),
            "HOME"
        );

        contentPanel.add(
            new BrowsePetsPanel(user),
            "BROWSE"
        );

        contentPanel.add(
            new MyApplicationsPanel(user),
            "MY_APPLICATIONS"
        );

        if (user.isAdmin()) {
            contentPanel.add(
                new PetManagementPanel(),
                "PETS"
            );

            contentPanel.add(
                new ApplicationManagementPanel(),
                "APPLICATIONS"
            );
        }

        cardLayout.show(
            contentPanel,
            "HOME"
        );
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(
            new Dimension(230, 0)
        );

        sidebar.setBackground(
            new Color(230, 242, 238)
        );

        sidebar.setBorder(
            BorderFactory.createEmptyBorder(
                25,
                15,
                25,
                15
            )
        );

        sidebar.setLayout(
            new BoxLayout(
                sidebar,
                BoxLayout.Y_AXIS
            )
        );

        JLabel logoLabel =
            new JLabel("PawHome");

        logoLabel.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                25
            )
        );

        logoLabel.setForeground(Color.BLACK);
        logoLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel welcomeLabel =
            new JLabel(
                "Welcome, " + user.fullName()
            );

        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setAlignmentX(
            LEFT_ALIGNMENT
        );

        sidebar.add(logoLabel);
        sidebar.add(
            Box.createVerticalStrut(8)
        );
        sidebar.add(welcomeLabel);
        sidebar.add(
            Box.createVerticalStrut(35)
        );

        addNavigationButton(
            sidebar,
            "Dashboard",
            "HOME"
        );

        addNavigationButton(
            sidebar,
            "Browse Pets",
            "BROWSE"
        );

        addNavigationButton(
            sidebar,
            "My Applications",
            "MY_APPLICATIONS"
        );

        if (user.isAdmin()) {
            addNavigationButton(
                sidebar,
                "Manage Pets",
                "PETS"
            );

            addNavigationButton(
                sidebar,
                "Manage Applications",
                "APPLICATIONS"
            );
        }

        sidebar.add(
            Box.createVerticalGlue()
        );

        JButton logoutButton =
            navigationButton("Logout");

        logoutButton.addActionListener(
            event -> {
                dispose();

                LoginFrame loginFrame =
                    new LoginFrame();

                loginFrame.setVisible(true);
            }
        );

        sidebar.add(logoutButton);

        return sidebar;
    }

    private void addNavigationButton(
        JPanel sidebar,
        String text,
        String pageName
    ) {
        JButton button =
            navigationButton(text);

        button.addActionListener(
            event -> {
                Component[] components =
                    contentPanel.getComponents();

                for (Component component : components) {
                    if (
                        component
                            instanceof Refreshable
                            refreshable
                    ) {
                        refreshable.refreshData();
                    }
                }

                cardLayout.show(
                    contentPanel,
                    pageName
                );
            }
        );

        sidebar.add(button);
        sidebar.add(
            Box.createVerticalStrut(8)
        );
    }

    private JButton navigationButton(
        String text
    ) {
        JButton button =
            new JButton(text);

        button.setMaximumSize(
            new Dimension(
                Integer.MAX_VALUE,
                42
            )
        );

        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setHorizontalAlignment(
            JButton.LEFT
        );

        button.setBackground(
            new Color(190, 225, 215)
        );

        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                13
            )
        );

        button.setBorder(
            BorderFactory.createEmptyBorder(
                10,
                14,
                10,
                14
            )
        );

        return button;
    }
}

class HomePanel extends JPanel
    implements Refreshable {

    private final JLabel availableLabel =
        createStatisticLabel();

    private final JLabel pendingLabel =
        createStatisticLabel();

    private final JLabel adoptedLabel =
        createStatisticLabel();

    private final JLabel usersLabel =
        createStatisticLabel();

    public HomePanel() {
        setLayout(
            new BorderLayout(0, 25)
        );

        setBackground(Style.LIGHT);

        setBorder(
            BorderFactory.createEmptyBorder(
                30,
                30,
                30,
                30
            )
        );

        JPanel headingPanel =
            new JPanel(new GridLayout(2, 1));

        headingPanel.setOpaque(false);

        headingPanel.add(
            Style.title("PawHome Dashboard")
        );

        JLabel messageLabel =
            new JLabel(
                "Helping rescued animals find " +
                "safe and loving homes."
            );

        messageLabel.setForeground(Color.BLACK);

        headingPanel.add(messageLabel);

        add(
            headingPanel,
            BorderLayout.NORTH
        );

        JPanel statisticsPanel =
            new JPanel(
                new GridLayout(2, 2, 20, 20)
            );

        statisticsPanel.setOpaque(false);

        statisticsPanel.add(
            createCard(
                "Available Pets",
                availableLabel
            )
        );

        statisticsPanel.add(
            createCard(
                "Pending Applications",
                pendingLabel
            )
        );

        statisticsPanel.add(
            createCard(
                "Adopted Pets",
                adoptedLabel
            )
        );

        statisticsPanel.add(
            createCard(
                "Registered Users",
                usersLabel
            )
        );

        add(
            statisticsPanel,
            BorderLayout.CENTER
        );

        refreshData();
    }

    private static JLabel createStatisticLabel() {
        JLabel label = new JLabel(
            "0",
            SwingConstants.CENTER
        );

        label.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                38
            )
        );

        label.setForeground(Style.GREEN);

        return label;
    }

    private JPanel createCard(
        String title,
        JLabel valueLabel
    ) {
        JPanel panel =
            new JPanel(new BorderLayout());

        panel.setBackground(Color.WHITE);

        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 230, 226)
                ),
                BorderFactory.createEmptyBorder(
                    25,
                    25,
                    25,
                    25
                )
            )
        );

        JLabel titleLabel =
            new JLabel(
                title,
                SwingConstants.CENTER
            );

        titleLabel.setFont(
            new Font(
                "SansSerif",
                Font.BOLD,
                17
            )
        );

        titleLabel.setForeground(Color.BLACK);

        panel.add(
            valueLabel,
            BorderLayout.CENTER
        );

        panel.add(
            titleLabel,
            BorderLayout.SOUTH
        );

        return panel;
    }

    public void refreshData() {
        try (
            Connection connection =
                Database.connect();

            Statement statement =
                connection.createStatement()
        ) {
            availableLabel.setText(
                getCount(
                    statement,
                    "SELECT COUNT(*) FROM pets " +
                    "WHERE status='AVAILABLE'"
                )
            );

            pendingLabel.setText(
                getCount(
                    statement,
                    "SELECT COUNT(*) " +
                    "FROM applications " +
                    "WHERE status='PENDING'"
                )
            );

            adoptedLabel.setText(
                getCount(
                    statement,
                    "SELECT COUNT(*) FROM pets " +
                    "WHERE status='ADOPTED'"
                )
            );

            usersLabel.setText(
                getCount(
                    statement,
                    "SELECT COUNT(*) FROM users " +
                    "WHERE role='USER'"
                )
            );
        } catch (Exception exception) {
            availableLabel.setText("-");
            pendingLabel.setText("-");
            adoptedLabel.setText("-");
            usersLabel.setText("-");
        }
    }

    private String getCount(
        Statement statement,
        String sql
    ) throws Exception {

        ResultSet result =
            statement.executeQuery(sql);

        if (result.next()) {
            return String.valueOf(
                result.getInt(1)
            );
        }

        return "0";
    }
}