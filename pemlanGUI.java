package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class pemlanGUI extends JFrame {
    private JTextField nameField, nimField, addressField;
    private JButton addButton, displayButton, deleteData;
    private JTable table;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pemlanGUI gui = new pemlanGUI();
                gui.createAndShowGUI();
            }
        });
    }

    public void createAndShowGUI() {

        JFrame frame = new JFrame("Data Entry");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(null);

        JLabel nameLabel = new JLabel("Nama:");
        nameLabel.setBounds(20, 20, 80, 25);
        frame.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(100, 20, 320, 25);
        frame.add(nameField);

        JLabel nimLabel = new JLabel("NIM:");
        nimLabel.setBounds(20, 50, 80, 25);
        frame.add(nimLabel);

        nimField = new JTextField();
        nimField.setBounds(100, 50, 320, 25);
        frame.add(nimField);

        JLabel addressLabel = new JLabel("Alamat:");
        addressLabel.setBounds(20, 80, 80, 25);
        frame.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(100, 80, 320, 25);
        frame.add(addressField);

        addButton = new JButton("Simpan");
        addButton.setBounds(100, 110, 80, 25);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });
        frame.add(addButton);

        deleteData = new JButton("Hapus");
        deleteData.setBounds(200, 110, 80, 25);
        deleteData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(pemlanGUI.this, "Pilih baris yang akan dihapus.", "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    String nim = (String) table.getValueAt(selectedRow, 1);
                    deleteData(nim);
                }
            }
        });
        frame.add(deleteData);

        displayButton = new JButton("Tampilkan");
        displayButton.setBounds(300, 110, 80, 25);
        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayUsers();
            }
        });
        frame.add(displayButton);

        String[] columnNames = { "Nama", "NIM", "Alamat" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(450, 20, 310, 420); 
        frame.add(scrollPane);

        frame.setVisible(true);
    }

    private void deleteData(String nim) {
        int option = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus data ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM mahasiswa WHERE nim = ?";
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nim);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                displayUsers();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting data.");
            }
        }
    }

    private void addUser() {
        String name = nameField.getText();
        String nim = nimField.getText();
        String address = addressField.getText();

        int option = JOptionPane
                .showConfirmDialog(
                        this, "Tambahkan pengguna dengan detail berikut?\nNama: " + name + "\nNIM: " + nim
                                + "\nAlamat: " + address,
                        "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                try {
                    String query = "INSERT INTO mahasiswa (nama, nim, alamat) VALUES (?, ?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, nim);
                    preparedStatement.setString(3, address);
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error menambahkan data.");
                }
            }
        }
    }

    private void displayUsers() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try {
                String query = "SELECT * FROM mahasiswa";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.setRowCount(0);
                while (rs.next()) {
                    String name = rs.getString("nama");
                    String nim = rs.getString("nim");
                    String address = rs.getString("alamat");
                    tableModel.addRow(new Object[] { name, nim, address });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
