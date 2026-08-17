package pawhome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public final class AdminPanel {
    private AdminPanel() {
    }
}

class PetManagementPanel extends JPanel
    implements Refreshable {

    private final JTextField nameField =
        new JTextField(13);

    private final JComboBox<String> typeBox =
        new JComboBox<>(
            new String[] {
                "Dog",
                "Cat",
                "Rabbit",
                "Other"
            }
        );

    private final JTextField breedField =
        new JTextField(13);

    private final JTextField ageField =
        new JTextField(13);

    private final JComboBox<String> genderBox =
        new JComboBox<>(
            new String[] {
                "Male",
                "Female"
            }
        );

    private final JTextField healthField =
        new JTextField(13);

    private final JComboBox<String> statusBox =
        new JComboBox<>(
            new String[] {
                "AVAILABLE",
                "PENDING",
                "ADOPTED"
            }
        );

    private final DefaultTableModel tableModel =
        new DefaultTableModel(
            new String[] {
                "ID",
                "Name",
                "Type",
                "Breed",
                "Age",
                "Gender",
                "Health",
                "Status"
            },
            0
        ) {
            public boolean isCellEditable(
                int row,
                int column
            ) {
                return false;
            }
        };

    private final JTable petTable =
        new JTable(tableModel);

    public PetManagementPanel() {
        setLayout(
            new BorderLayout(0, 15)
        );

        setBackground(Style.LIGHT);

        setBorder(
            BorderFactory.createEmptyBorder(
                20,
                20,
                20,
                20
            )
        );

        JPanel topPanel =
            new JPanel(new BorderLayout());

        topPanel.setOpaque(false);

        topPanel.add(
            Style.title("Pet Management"),
            BorderLayout.NORTH
        );

        JPanel formPanel =
            new JPanel(new GridBagLayout());

        formPanel.setOpaque(false);

        addField(
            formPanel,
            "Name",
            nameField,
            0,
            0
        );

        addField(
            formPanel,
            "Type",
            typeBox,
            2,
            0
        );

        addField(
            formPanel,
            "Breed",
            breedField,
            4,
            0
        );

        addField(
            formPanel,
            "Age",
            ageField,
            0,
            1
        );

        addField(
            formPanel,
            "Gender",
            genderBox,
            2,
            1
        );

        addField(
            formPanel,
            "Health",
            healthField,
            4,
            1
        );

        addField(
            formPanel,
            "Status",
            statusBox,
            0,
            2
        );

        JButton addButton =
            Style.button("Add");

        JButton updateButton =
            Style.button("Update");

        JButton deleteButton =
            Style.button("Delete");

        JButton clearButton =
            Style.button("Clear");

        addButton.setForeground(Color.BLACK);
        updateButton.setForeground(Color.BLACK);
        deleteButton.setForeground(Color.BLACK);
        clearButton.setForeground(Color.BLACK);

        addButton.setBackground(
            new Color(190, 225, 215)
        );

        updateButton.setBackground(
            new Color(190, 225, 215)
        );

        deleteButton.setBackground(
            new Color(245, 190, 190)
        );

        clearButton.setBackground(
            new Color(215, 220, 218)
        );

        JPanel buttonPanel = new JPanel();

        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        formPanel.add(
            buttonPanel,
            Style.constraints(3, 2)
        );

        topPanel.add(
            formPanel,
            BorderLayout.CENTER
        );

        add(
            topPanel,
            BorderLayout.NORTH
        );

        Style.field(nameField);
        Style.field(breedField);
        Style.field(ageField);
        Style.field(healthField);
        Style.table(petTable);

        add(
            new JScrollPane(petTable),
            BorderLayout.CENTER
        );

        addButton.addActionListener(
            event -> savePet(false)
        );

        updateButton.addActionListener(
            event -> savePet(true)
        );

        deleteButton.addActionListener(
            event -> deletePet()
        );

        clearButton.addActionListener(
            event -> clearForm()
        );

        petTable
            .getSelectionModel()
            .addListSelectionListener(
                event -> fillForm()
            );

        refreshData();
    }

    private void addField(
        JPanel panel,
        String text,
        Component field,
        int x,
        int y
    ) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLACK);

        panel.add(
            label,
            Style.constraints(x, y)
        );

        panel.add(
            field,
            Style.constraints(x + 1, y)
        );
    }

    private void savePet(boolean updating) {
        String name =
            nameField.getText().trim();

        String breed =
            breedField.getText().trim();

        String ageText =
            ageField.getText().trim();

        String health =
            healthField.getText().trim();

        if (
            name.isEmpty() ||
            breed.isEmpty() ||
            ageText.isEmpty() ||
            health.isEmpty()
        ) {
            JOptionPane.showMessageDialog(
                this,
                "Please complete all pet information.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int age;

        try {
            age = Integer.parseInt(ageText);

            if (age < 0 || age > 40) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                this,
                "Age must be a number between 0 and 40.",
                "Invalid Age",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int selectedRow =
            petTable.getSelectedRow();

        if (updating && selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Select a pet to update.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql;

        if (updating) {
            sql =
                "UPDATE pets SET " +
                "name=?, type=?, breed=?, age=?, " +
                "gender=?, health=?, status=? " +
                "WHERE id=?";
        } else {
            sql =
                "INSERT INTO pets(" +
                "name, type, breed, age, gender, " +
                "health, status" +
                ") VALUES(?,?,?,?,?,?,?)";
        }

        try (
            Connection connection =
                Database.connect();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);

            statement.setString(
                2,
                String.valueOf(
                    typeBox.getSelectedItem()
                )
            );

            statement.setString(3, breed);
            statement.setInt(4, age);

            statement.setString(
                5,
                String.valueOf(
                    genderBox.getSelectedItem()
                )
            );

            statement.setString(6, health);

            statement.setString(
                7,
                String.valueOf(
                    statusBox.getSelectedItem()
                )
            );

            if (updating) {
                statement.setInt(
                    8,
                    (int) tableModel.getValueAt(
                        selectedRow,
                        0
                    )
                );
            }

            statement.executeUpdate();

            refreshData();
            clearForm();

            JOptionPane.showMessageDialog(
                this,
                updating
                    ? "Pet record updated."
                    : "Pet record added."
            );
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to save pet: " +
                exception.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deletePet() {
        int selectedRow =
            petTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Select a pet to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int confirmation =
            JOptionPane.showConfirmDialog(
                this,
                "Delete this pet record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );

        if (
            confirmation !=
            JOptionPane.YES_OPTION
        ) {
            return;
        }

        int petId =
            (int) tableModel.getValueAt(
                selectedRow,
                0
            );

        try (
            Connection connection =
                Database.connect();

            PreparedStatement statement =
                connection.prepareStatement(
                    "DELETE FROM pets WHERE id=?"
                )
        ) {
            statement.setInt(1, petId);
            statement.executeUpdate();

            refreshData();
            clearForm();

            JOptionPane.showMessageDialog(
                this,
                "Pet record deleted."
            );
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                "This pet cannot be deleted because " +
                "it has adoption application records.",
                "Delete Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void fillForm() {
        int selectedRow =
            petTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        nameField.setText(
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    1
                )
            )
        );

        typeBox.setSelectedItem(
            tableModel.getValueAt(
                selectedRow,
                2
            )
        );

        breedField.setText(
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    3
                )
            )
        );

        ageField.setText(
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    4
                )
            )
        );

        genderBox.setSelectedItem(
            tableModel.getValueAt(
                selectedRow,
                5
            )
        );

        healthField.setText(
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    6
                )
            )
        );

        statusBox.setSelectedItem(
            tableModel.getValueAt(
                selectedRow,
                7
            )
        );
    }

    private void clearForm() {
        petTable.clearSelection();
        nameField.setText("");
        breedField.setText("");
        ageField.setText("");
        healthField.setText("");
        typeBox.setSelectedIndex(0);
        genderBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
    }

    public void refreshData() {
        tableModel.setRowCount(0);

        String sql =
            "SELECT * FROM pets ORDER BY id";

        try (
            Connection connection =
                Database.connect();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {
            ResultSet result =
                statement.executeQuery();

            while (result.next()) {
                tableModel.addRow(
                    new Object[] {
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("type"),
                        result.getString("breed"),
                        result.getInt("age"),
                        result.getString("gender"),
                        result.getString("health"),
                        result.getString("status")
                    }
                );
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to load pets: " +
                exception.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

class ApplicationManagementPanel
    extends JPanel
    implements Refreshable {

    private final DefaultTableModel tableModel =
        new DefaultTableModel(
            new String[] {
                "ID",
                "Applicant",
                "Phone",
                "Pet ID",
                "Pet",
                "Date",
                "Home",
                "Own Pets",
                "Reason",
                "Status"
            },
            0
        ) {
            public boolean isCellEditable(
                int row,
                int column
            ) {
                return false;
            }
        };

    private final JTable applicationTable =
        new JTable(tableModel);

    public ApplicationManagementPanel() {
        setLayout(
            new BorderLayout(0, 18)
        );

        setBackground(Style.LIGHT);

        setBorder(
            BorderFactory.createEmptyBorder(
                25,
                25,
                25,
                25
            )
        );

        add(
            Style.title(
                "Adoption Application Management"
            ),
            BorderLayout.NORTH
        );

        Style.table(applicationTable);

        add(
            new JScrollPane(applicationTable),
            BorderLayout.CENTER
        );

        JButton approveButton =
            Style.button("Approve");

        JButton rejectButton =
            Style.button("Reject");

        approveButton.setForeground(Color.BLACK);
        rejectButton.setForeground(Color.BLACK);

        approveButton.setBackground(
            new Color(190, 225, 215)
        );

        rejectButton.setBackground(
            new Color(245, 190, 190)
        );

        JPanel actionPanel =
            new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
            );

        actionPanel.setOpaque(false);
        actionPanel.add(approveButton);
        actionPanel.add(rejectButton);

        add(
            actionPanel,
            BorderLayout.SOUTH
        );

        approveButton.addActionListener(
            event ->
                processApplication("APPROVED")
        );

        rejectButton.addActionListener(
            event ->
                processApplication("REJECTED")
        );

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);

        String sql =
            "SELECT a.id, u.full_name, u.phone, " +
            "p.id, p.name, a.application_date, " +
            "a.home_type, a.has_pets, a.reason, " +
            "a.status " +
            "FROM applications a " +
            "JOIN users u ON a.user_id=u.id " +
            "JOIN pets p ON a.pet_id=p.id " +
            "ORDER BY CASE a.status " +
            "WHEN 'PENDING' THEN 0 ELSE 1 END, " +
            "a.id DESC";

        try (
            Connection connection =
                Database.connect();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {
            ResultSet result =
                statement.executeQuery();

            while (result.next()) {
                tableModel.addRow(
                    new Object[] {
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4),
                        result.getString(5),
                        result.getString(6),
                        result.getString(7),
                        result.getString(8),
                        result.getString(9),
                        result.getString(10)
                    }
                );
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to load applications: " +
                exception.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void processApplication(
        String newStatus
    ) {
        int selectedRow =
            applicationTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an application.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String currentStatus =
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    9
                )
            );

        if (!"PENDING".equals(currentStatus)) {
            JOptionPane.showMessageDialog(
                this,
                "Only pending applications " +
                "can be processed.",
                "Already Processed",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int applicationId =
            (int) tableModel.getValueAt(
                selectedRow,
                0
            );

        int petId =
            (int) tableModel.getValueAt(
                selectedRow,
                3
            );

        String action =
            newStatus.substring(0, 1) +
            newStatus.substring(1).toLowerCase();

        int confirmation =
            JOptionPane.showConfirmDialog(
                this,
                action + " this application?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );

        if (
            confirmation !=
            JOptionPane.YES_OPTION
        ) {
            return;
        }

        try (
            Connection connection =
                Database.connect()
        ) {
            connection.setAutoCommit(false);

            try {
                String applicationSql =
                    "UPDATE applications " +
                    "SET status=? WHERE id=?";

                try (
                    PreparedStatement statement =
                        connection.prepareStatement(
                            applicationSql
                        )
                ) {
                    statement.setString(
                        1,
                        newStatus
                    );

                    statement.setInt(
                        2,
                        applicationId
                    );

                    statement.executeUpdate();
                }

                if ("APPROVED".equals(newStatus)) {
                    String petSql =
                        "UPDATE pets " +
                        "SET status='ADOPTED' " +
                        "WHERE id=?";

                    try (
                        PreparedStatement statement =
                            connection.prepareStatement(
                                petSql
                            )
                    ) {
                        statement.setInt(1, petId);
                        statement.executeUpdate();
                    }

                    String otherApplicationsSql =
                        "UPDATE applications " +
                        "SET status='REJECTED' " +
                        "WHERE pet_id=? " +
                        "AND id<>? " +
                        "AND status='PENDING'";

                    try (
                        PreparedStatement statement =
                            connection.prepareStatement(
                                otherApplicationsSql
                            )
                    ) {
                        statement.setInt(1, petId);
                        statement.setInt(
                            2,
                            applicationId
                        );

                        statement.executeUpdate();
                    }
                }

                connection.commit();
                refreshData();

                JOptionPane.showMessageDialog(
                    this,
                    "Application " +
                    newStatus.toLowerCase() +
                    " successfully."
                );
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to process application: " +
                exception.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}