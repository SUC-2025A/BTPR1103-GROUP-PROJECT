package pawhome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public final class UserPanel {
    private UserPanel() {
    }
}

class BrowsePetsPanel extends JPanel
    implements Refreshable {

    private final User user;

    private final JTextField searchField =
        new JTextField(18);

    private final JComboBox<String> typeBox =
        new JComboBox<>(
            new String[] {
                "All Types",
                "Dog",
                "Cat",
                "Rabbit",
                "Other"
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

    private final PetPreviewPanel previewPanel =
        new PetPreviewPanel();

    public BrowsePetsPanel(User user) {
        this.user = user;

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

        JPanel topPanel =
            new JPanel(new BorderLayout());

        topPanel.setOpaque(false);

        topPanel.add(
            Style.title("Browse Available Pets"),
            BorderLayout.NORTH
        );

        JPanel filterPanel =
            new JPanel(
                new FlowLayout(FlowLayout.LEFT)
            );

        filterPanel.setOpaque(false);

        JLabel searchLabel =
            new JLabel("Search");

        searchLabel.setForeground(Color.BLACK);

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(typeBox);

        JButton searchButton =
            Style.button("Search");

        JButton resetButton =
            Style.button("Reset");

        searchButton.setForeground(Color.BLACK);
        resetButton.setForeground(Color.BLACK);

        resetButton.setBackground(
            new Color(215, 220, 218)
        );

        filterPanel.add(searchButton);
        filterPanel.add(resetButton);

        topPanel.add(
            filterPanel,
            BorderLayout.SOUTH
        );

        add(
            topPanel,
            BorderLayout.NORTH
        );

        Style.field(searchField);
        Style.table(petTable);

        JPanel browsingPanel =
            new JPanel(
                new BorderLayout(18, 0)
            );

        browsingPanel.setOpaque(false);

        browsingPanel.add(
            new JScrollPane(petTable),
            BorderLayout.CENTER
        );

        browsingPanel.add(
            previewPanel,
            BorderLayout.EAST
        );

        add(
            browsingPanel,
            BorderLayout.CENTER
        );

        JButton applyButton =
            Style.button(
                "Apply to Adopt Selected Pet"
            );

        applyButton.setForeground(Color.BLACK);

        JPanel bottomPanel =
            new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
            );

        bottomPanel.setOpaque(false);
        bottomPanel.add(applyButton);

        add(
            bottomPanel,
            BorderLayout.SOUTH
        );

        searchButton.addActionListener(
            event -> loadPets()
        );

        resetButton.addActionListener(
            event -> {
                searchField.setText("");
                typeBox.setSelectedIndex(0);
                loadPets();
            }
        );

        applyButton.addActionListener(
            event -> applyForAdoption()
        );

        petTable
            .getSelectionModel()
            .addListSelectionListener(
                event -> updatePreview()
            );

        loadPets();
    }

    private void loadPets() {
        tableModel.setRowCount(0);

        String keyword =
            "%" +
            searchField.getText().trim() +
            "%";

        String selectedType =
            String.valueOf(
                typeBox.getSelectedItem()
            );

        String sql =
            "SELECT * FROM pets " +
            "WHERE (name LIKE ? OR breed LIKE ?) " +
            "AND (?='All Types' OR type=?) " +
            "ORDER BY id";

        try (
            Connection connection =
                Database.connect();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {
            statement.setString(1, keyword);
            statement.setString(2, keyword);
            statement.setString(
                3,
                selectedType
            );
            statement.setString(
                4,
                selectedType
            );

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

    private void updatePreview() {
        int selectedRow =
            petTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        previewPanel.showPet(
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    1
                )
            ),
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    2
                )
            ),
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    3
                )
            ),
            (int) tableModel.getValueAt(
                selectedRow,
                4
            ),
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    5
                )
            ),
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    6
                )
            ),
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    7
                )
            )
        );
    }

    private void applyForAdoption() {
        int selectedRow =
            petTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a pet first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String status =
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    7
                )
            );

        if (!"AVAILABLE".equals(status)) {
            JOptionPane.showMessageDialog(
                this,
                "This pet is not available for adoption.",
                "Unavailable Pet",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int petId =
            (int) tableModel.getValueAt(
                selectedRow,
                0
            );

        String petName =
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    1
                )
            );

        AdoptionDialog dialog =
            new AdoptionDialog(
                user,
                petId,
                petName,
                this
            );

        dialog.setVisible(true);
    }

    public void refreshData() {
        loadPets();
    }
}

class PetPreviewPanel extends JPanel {
    private final JLabel nameLabel =
        new JLabel(
            "Choose a pet",
            SwingConstants.CENTER
        );

    private final JLabel detailsLabel =
        new JLabel(
            "Select a row to view its profile",
            SwingConstants.CENTER
        );

    private final PetImagePanel imagePanel =
        new PetImagePanel();

    public PetPreviewPanel() {
        setLayout(
            new BorderLayout(0, 12)
        );

        setBackground(Color.WHITE);

        setPreferredSize(
            new Dimension(290, 0)
        );

        setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(210, 225, 220)
                ),
                BorderFactory.createEmptyBorder(
                    18,
                    18,
                    18,
                    18
                )
            )
        );

        nameLabel.setFont(
            new java.awt.Font(
                "SansSerif",
                java.awt.Font.BOLD,
                24
            )
        );

        nameLabel.setForeground(Color.BLACK);
        detailsLabel.setForeground(Color.BLACK);

        add(
            nameLabel,
            BorderLayout.NORTH
        );

        add(
            imagePanel,
            BorderLayout.CENTER
        );

        add(
            detailsLabel,
            BorderLayout.SOUTH
        );
    }

    public void showPet(
        String name,
        String type,
        String breed,
        int age,
        String gender,
        String health,
        String status
    ) {
        nameLabel.setText(name);

        detailsLabel.setText(
            "<html>" +
            "<div style='text-align:center'>" +
            "<b>" +
            type +
            " · " +
            breed +
            "</b><br>" +
            age +
            " year(s) · " +
            gender +
            "<br>" +
            health +
            "<br><br>" +
            "<b>Status: " +
            status +
            "</b>" +
            "</div>" +
            "</html>"
        );

        imagePanel.setPetType(type);
    }
}

class PetImagePanel extends JPanel {
    private BufferedImage spriteImage;
    private String petType = "Dog";

    public PetImagePanel() {
        setOpaque(false);

        try {
            spriteImage = ImageIO.read(
                PetImagePanel.class.getResource(
                    "/pawhome/pet-sprites.png"
                )
            );
        } catch (
            IOException |
            IllegalArgumentException exception
        ) {
            spriteImage = null;
        }
    }

    public void setPetType(
        String petType
    ) {
        this.petType = petType;
        repaint();
    }

    protected void paintComponent(
        Graphics graphics
    ) {
        super.paintComponent(graphics);

        if (spriteImage == null) {
            return;
        }

        int column = 0;
        int row = 0;

        if ("Cat".equals(petType)) {
            column = 1;
            row = 0;
        } else if ("Rabbit".equals(petType)) {
            column = 0;
            row = 1;
        } else if ("Other".equals(petType)) {
            column = 1;
            row = 1;
        }

        int sourceWidth =
            spriteImage.getWidth() / 2;

        int sourceHeight =
            spriteImage.getHeight() / 2;

        BufferedImage animalImage =
            spriteImage.getSubimage(
                column * sourceWidth,
                row * sourceHeight,
                sourceWidth,
                sourceHeight
            );

        int imageSize =
            Math.min(
                getWidth(),
                getHeight()
            );

        int imageX =
            (getWidth() - imageSize) / 2;

        int imageY =
            (getHeight() - imageSize) / 2;

        Graphics2D graphics2D =
            (Graphics2D) graphics.create();

        graphics2D.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints
                .VALUE_INTERPOLATION_BICUBIC
        );

        Image scaledImage =
            animalImage.getScaledInstance(
                imageSize,
                imageSize,
                Image.SCALE_SMOOTH
            );

        graphics2D.drawImage(
            scaledImage,
            imageX,
            imageY,
            null
        );

        graphics2D.dispose();
    }
}

class AdoptionDialog extends JDialog {
    private final User user;
    private final int petId;
    private final Refreshable parentPanel;

    private final JTextArea reasonArea =
        new JTextArea(5, 22);

    private final JComboBox<String> homeBox =
        new JComboBox<>(
            new String[] {
                "Apartment",
                "Terrace House",
                "Semi-Detached House",
                "Detached House",
                "Other"
            }
        );

    private final JComboBox<String> petsBox =
        new JComboBox<>(
            new String[] {
                "No",
                "Yes"
            }
        );

    public AdoptionDialog(
        User user,
        int petId,
        String petName,
        Refreshable parentPanel
    ) {
        this.user = user;
        this.petId = petId;
        this.parentPanel = parentPanel;

        setTitle("Adoption Application");
        setModal(true);
        setSize(520, 470);
        setLocationRelativeTo(null);

        JPanel formPanel = Style.page();

        formPanel.setLayout(
            new GridBagLayout()
        );

        formPanel.add(
            Style.title("Adopt " + petName),
            Style.constraints(0, 0)
        );

        addLabel(
            formPanel,
            "Applicant",
            1
        );

        JLabel applicantLabel =
            new JLabel(user.fullName());

        applicantLabel.setForeground(Color.BLACK);

        formPanel.add(
            applicantLabel,
            Style.constraints(1, 1)
        );

        addLabel(
            formPanel,
            "Home Type",
            2
        );

        formPanel.add(
            homeBox,
            Style.constraints(1, 2)
        );

        addLabel(
            formPanel,
            "Currently Own Pets",
            3
        );

        formPanel.add(
            petsBox,
            Style.constraints(1, 3)
        );

        addLabel(
            formPanel,
            "Reason for Adoption",
            4
        );

        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setForeground(Color.BLACK);
        reasonArea.setBackground(Color.WHITE);

        formPanel.add(
            new JScrollPane(reasonArea),
            Style.constraints(1, 4)
        );

        JButton submitButton =
            Style.button("Submit Application");

        submitButton.setForeground(Color.BLACK);

        formPanel.add(
            submitButton,
            Style.constraints(1, 5)
        );

        submitButton.addActionListener(
            event -> submitApplication()
        );

        add(formPanel);
    }

    private void addLabel(
        JPanel panel,
        String text,
        int row
    ) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLACK);

        panel.add(
            label,
            Style.constraints(0, row)
        );
    }

    private void submitApplication() {
        String reason =
            reasonArea.getText().trim();

        if (reason.length() < 15) {
            JOptionPane.showMessageDialog(
                this,
                "Please provide a reason of at least 15 characters.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try (
            Connection connection =
                Database.connect()
        ) {
            String duplicateSql =
                "SELECT COUNT(*) " +
                "FROM applications " +
                "WHERE user_id=? " +
                "AND pet_id=? " +
                "AND status='PENDING'";

            try (
                PreparedStatement checkStatement =
                    connection.prepareStatement(
                        duplicateSql
                    )
            ) {
                checkStatement.setInt(
                    1,
                    user.id()
                );

                checkStatement.setInt(
                    2,
                    petId
                );

                ResultSet result =
                    checkStatement.executeQuery();

                if (
                    result.next() &&
                    result.getInt(1) > 0
                ) {
                    JOptionPane.showMessageDialog(
                        this,
                        "You already have a pending application for this pet.",
                        "Duplicate Application",
                        JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }
            }

            String petSql =
                "SELECT status FROM pets " +
                "WHERE id=?";

            try (
                PreparedStatement petStatement =
                    connection.prepareStatement(
                        petSql
                    )
            ) {
                petStatement.setInt(1, petId);

                ResultSet result =
                    petStatement.executeQuery();

                if (
                    !result.next() ||
                    !"AVAILABLE".equals(
                        result.getString("status")
                    )
                ) {
                    JOptionPane.showMessageDialog(
                        this,
                        "This pet is no longer available.",
                        "Unavailable Pet",
                        JOptionPane.WARNING_MESSAGE
                    );

                    parentPanel.refreshData();
                    return;
                }
            }

            String insertSql =
                "INSERT INTO applications(" +
                "user_id, pet_id, reason, " +
                "home_type, has_pets, " +
                "application_date, status" +
                ") VALUES(?,?,?,?,?,?,?)";

            try (
                PreparedStatement insertStatement =
                    connection.prepareStatement(
                        insertSql
                    )
            ) {
                insertStatement.setInt(
                    1,
                    user.id()
                );

                insertStatement.setInt(
                    2,
                    petId
                );

                insertStatement.setString(
                    3,
                    reason
                );

                insertStatement.setString(
                    4,
                    String.valueOf(
                        homeBox.getSelectedItem()
                    )
                );

                insertStatement.setString(
                    5,
                    String.valueOf(
                        petsBox.getSelectedItem()
                    )
                );

                insertStatement.setString(
                    6,
                    LocalDate.now().toString()
                );

                insertStatement.setString(
                    7,
                    "PENDING"
                );

                insertStatement.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                this,
                "Your adoption application " +
                "has been submitted."
            );

            parentPanel.refreshData();
            dispose();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                "Application failed: " +
                exception.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

class MyApplicationsPanel extends JPanel
    implements Refreshable {

    private final User user;

    private final DefaultTableModel tableModel =
        new DefaultTableModel(
            new String[] {
                "Application ID",
                "Pet",
                "Type",
                "Date",
                "Home Type",
                "Own Pets",
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

    public MyApplicationsPanel(User user) {
        this.user = user;

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
                "My Adoption Applications"
            ),
            BorderLayout.NORTH
        );

        Style.table(applicationTable);

        add(
            new JScrollPane(applicationTable),
            BorderLayout.CENTER
        );

        JButton cancelButton =
            Style.button(
                "Cancel Selected Application"
            );

        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBackground(
            new Color(245, 190, 190)
        );

        JPanel actionPanel =
            new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
            );

        actionPanel.setOpaque(false);
        actionPanel.add(cancelButton);

        add(
            actionPanel,
            BorderLayout.SOUTH
        );

        cancelButton.addActionListener(
            event -> cancelApplication()
        );

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);

        String sql =
            "SELECT a.id, p.name, p.type, " +
            "a.application_date, a.home_type, " +
            "a.has_pets, a.status " +
            "FROM applications a " +
            "JOIN pets p ON a.pet_id=p.id " +
            "WHERE a.user_id=? " +
            "ORDER BY a.id DESC";

        try (
            Connection connection =
                Database.connect();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {
            statement.setInt(
                1,
                user.id()
            );

            ResultSet result =
                statement.executeQuery();

            while (result.next()) {
                tableModel.addRow(
                    new Object[] {
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5),
                        result.getString(6),
                        result.getString(7)
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

    private void cancelApplication() {
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

        String status =
            String.valueOf(
                tableModel.getValueAt(
                    selectedRow,
                    6
                )
            );

        if (!"PENDING".equals(status)) {
            JOptionPane.showMessageDialog(
                this,
                "Only pending applications can be cancelled.",
                "Cannot Cancel",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int confirmation =
            JOptionPane.showConfirmDialog(
                this,
                "Cancel this application?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
            );

        if (
            confirmation !=
            JOptionPane.YES_OPTION
        ) {
            return;
        }

        int applicationId =
            (int) tableModel.getValueAt(
                selectedRow,
                0
            );

        String sql =
            "UPDATE applications " +
            "SET status='CANCELLED' " +
            "WHERE id=? AND user_id=?";

        try (
            Connection connection =
                Database.connect();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {
            statement.setInt(
                1,
                applicationId
            );

            statement.setInt(
                2,
                user.id()
            );

            statement.executeUpdate();

            refreshData();

            JOptionPane.showMessageDialog(
                this,
                "Application cancelled."
            );
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to cancel: " +
                exception.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}