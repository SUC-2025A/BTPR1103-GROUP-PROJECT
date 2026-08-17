package pawhome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {
    private final JTextField usernameField =
        new JTextField(20);

    private final JPasswordField passwordField =
        new JPasswordField(20);

    public LoginFrame() {
        setTitle("PawHome - Login");
        setSize(520, 430);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel outerPanel = Style.page();
        outerPanel.setLayout(new BorderLayout());

        JPanel formPanel =
            new JPanel(new GridBagLayout());

        formPanel.setBackground(Color.WHITE);
        formPanel.setForeground(Color.BLACK);

        formPanel.setBorder(
            BorderFactory.createEmptyBorder(
                30,
                45,
                30,
                45
            )
        );

        JLabel titleLabel = Style.title("PawHome");

        JLabel subtitleLabel = new JLabel(
            "Community Pet Adoption Management System"
        );

        subtitleLabel.setForeground(Color.BLACK);

        JLabel usernameLabel =
            new JLabel("Username");

        usernameLabel.setForeground(Color.BLACK);

        JLabel passwordLabel =
            new JLabel("Password");

        passwordLabel.setForeground(Color.BLACK);

        formPanel.add(
            titleLabel,
            Style.constraints(0, 0)
        );

        formPanel.add(
            subtitleLabel,
            Style.constraints(0, 1)
        );

        formPanel.add(
            usernameLabel,
            Style.constraints(0, 2)
        );

        formPanel.add(
            usernameField,
            Style.constraints(0, 3)
        );

        formPanel.add(
            passwordLabel,
            Style.constraints(0, 4)
        );

        formPanel.add(
            passwordField,
            Style.constraints(0, 5)
        );

        Style.field(usernameField);
        Style.field(passwordField);

        JButton loginButton =
            Style.button("Login");

        JButton registerButton =
            Style.button("Create Account");

        loginButton.setForeground(Color.BLACK);
        registerButton.setForeground(Color.BLACK);

        loginButton.setBackground(
            new Color(190, 225, 215)
        );

        registerButton.setBackground(
            new Color(215, 220, 218)
        );

        JPanel actionPanel = new JPanel();

        actionPanel.setBackground(Color.WHITE);
        actionPanel.add(loginButton);
        actionPanel.add(registerButton);

        formPanel.add(
            actionPanel,
            Style.constraints(0, 6)
        );

        outerPanel.add(
            formPanel,
            BorderLayout.CENTER
        );

        add(outerPanel);

        loginButton.addActionListener(
            event -> login()
        );

        registerButton.addActionListener(
            event -> {
                RegisterDialog dialog =
                    new RegisterDialog(this);

                dialog.setVisible(true);
            }
        );

        passwordField.addActionListener(
            event -> login()
        );

        getRootPane().setDefaultButton(
            loginButton
        );

        formPanel.setPreferredSize(
            new Dimension(420, 340)
        );
    }

    private void login() {
        String username =
            usernameField.getText().trim();

        String password =
            new String(
                passwordField.getPassword()
            );

        if (
            username.isEmpty() ||
            password.isEmpty()
        ) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter your username and password.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
            "SELECT id, full_name, phone, " +
            "username, role " +
            "FROM users " +
            "WHERE username = ? AND password = ?";

        try (
            Connection connection =
                Database.connect();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result =
                statement.executeQuery();

            if (result.next()) {
                User user = new User(
                    result.getInt("id"),
                    result.getString("full_name"),
                    result.getString("phone"),
                    result.getString("username"),
                    result.getString("role")
                );

                dispose();

                DashboardFrame dashboard =
                    new DashboardFrame(user);

                dashboard.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Incorrect username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to connect to the database: " +
                exception.getMessage(),
                "System Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

class RegisterDialog extends JDialog {
    private final JTextField nameField =
        new JTextField(20);

    private final JTextField phoneField =
        new JTextField(20);

    private final JTextField usernameField =
        new JTextField(20);

    private final JPasswordField passwordField =
        new JPasswordField(20);

    private final JPasswordField confirmField =
        new JPasswordField(20);

    public RegisterDialog(
        LoginFrame owner
    ) {
        super(
            owner,
            "Create Account",
            true
        );

        setSize(480, 480);
        setLocationRelativeTo(owner);

        JPanel formPanel = Style.page();
        formPanel.setLayout(
            new GridBagLayout()
        );

        formPanel.add(
            Style.title("Create Account"),
            Style.constraints(0, 0)
        );

        addRow(
            formPanel,
            "Full Name",
            nameField,
            1
        );

        addRow(
            formPanel,
            "Phone Number",
            phoneField,
            2
        );

        addRow(
            formPanel,
            "Username",
            usernameField,
            3
        );

        addRow(
            formPanel,
            "Password",
            passwordField,
            4
        );

        addRow(
            formPanel,
            "Confirm Password",
            confirmField,
            5
        );

        JButton createButton =
            Style.button("Register");

        createButton.setForeground(Color.BLACK);
        createButton.setBackground(
            new Color(190, 225, 215)
        );

        formPanel.add(
            createButton,
            Style.constraints(1, 6)
        );

        createButton.addActionListener(
            event -> register()
        );

        add(formPanel);
    }

    private void addRow(
        JPanel panel,
        String text,
        JTextField field,
        int row
    ) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLACK);

        panel.add(
            label,
            Style.constraints(0, row)
        );

        Style.field(field);

        panel.add(
            field,
            Style.constraints(1, row)
        );
    }

    private void register() {
        String name =
            nameField.getText().trim();

        String phone =
            phoneField.getText().trim();

        String username =
            usernameField.getText().trim();

        String password =
            new String(
                passwordField.getPassword()
            );

        String confirmation =
            new String(
                confirmField.getPassword()
            );

        if (
            name.isEmpty() ||
            phone.isEmpty() ||
            username.isEmpty() ||
            password.isEmpty() ||
            confirmation.isEmpty()
        ) {
            JOptionPane.showMessageDialog(
                this,
                "All fields are required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (!phone.matches("[0-9]{9,12}")) {
            JOptionPane.showMessageDialog(
                this,
                "Phone number must contain 9 to 12 digits.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (
            username.length() < 4 ||
            password.length() < 6
        ) {
            JOptionPane.showMessageDialog(
                this,
                "Username needs at least 4 characters " +
                "and password needs at least 6 characters.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (!password.equals(confirmation)) {
            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
            "INSERT INTO users(" +
            "full_name, phone, username, " +
            "password, role" +
            ") VALUES(?,?,?,?,?)";

        try (
            Connection connection =
                Database.connect();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, username);
            statement.setString(4, password);
            statement.setString(5, "USER");

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                "Account created successfully."
            );

            dispose();
        } catch (SQLException exception) {
            String errorMessage =
                exception.getMessage();

            if (
                errorMessage != null &&
                errorMessage.toLowerCase()
                    .contains("unique")
            ) {
                JOptionPane.showMessageDialog(
                    this,
                    "This username is already registered.",
                    "Duplicate Username",
                    JOptionPane.ERROR_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Registration failed: " +
                    errorMessage,
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}