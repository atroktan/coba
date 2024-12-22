import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class FinanceManagerGUI {

    private JFrame frame;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JTextField descriptionField, amountField;
    private JComboBox<String> typeComboBox;
    private JLabel balanceLabel;

    private int balance = 0;

    public FinanceManagerGUI() {
        initialize();
    }

    private void initialize() {
        // Frame Utama
        frame = new JFrame("Aplikasi Pengelola Keuangan Pribadi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        // Panel Atas: Menampilkan Saldo
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.CYAN);
        balanceLabel = new JLabel("Total Saldo: Rp 0");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(balanceLabel);
        frame.add(topPanel, BorderLayout.NORTH);

        // Tabel untuk Daftar Transaksi
        String[] columnNames = {"Keterangan", "Nominal", "Jenis", "Gambar"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) {
                    return ImageIcon.class; // Kolom gambar menggunakan ImageIcon
                }
                return super.getColumnClass(column);
            }
        };
        transactionTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Panel Bawah: Input Transaksi
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Margin antar komponen
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label "Keterangan"
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Keterangan:"), gbc);

        // Field "Keterangan"
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        descriptionField = new JTextField();
        inputPanel.add(descriptionField, gbc);
        gbc.gridwidth = 1;

        // Label "Nominal"
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Nominal:"), gbc);

        // Field "Nominal"
        gbc.gridx = 1;
        gbc.gridy = 1;
        amountField = new JTextField();
        inputPanel.add(amountField, gbc);

        // Label "Jenis"
        gbc.gridx = 2;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Jenis:"), gbc);

        // ComboBox "Jenis"
        gbc.gridx = 3;
        gbc.gridy = 1;
        typeComboBox = new JComboBox<>(new String[]{"Pemasukan", "Pengeluaran"});
        inputPanel.add(typeComboBox, gbc);

        // Tombol "Tambah Transaksi"
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton addButton = new JButton("Tambah Transaksi");
        inputPanel.add(addButton, gbc);
        gbc.gridwidth = 1;

        // Tombol "Hapus Transaksi"
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton deleteButton = new JButton("Hapus Transaksi");
        inputPanel.add(deleteButton, gbc);
        gbc.gridwidth = 1;

        // Tombol "Upload Gambar"
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        JButton uploadImageButton = new JButton("Upload Gambar");
        inputPanel.add(uploadImageButton, gbc);

        frame.add(inputPanel, BorderLayout.SOUTH);

        // Event Listener untuk Tombol Tambah
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction();
            }
        });

        // Event Listener untuk Tombol Hapus
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTransaction();
            }
        });

        // Event Listener untuk Upload Gambar
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadImage();
            }
        });

        frame.setVisible(true);
    }

    private void addTransaction() {
        String description = descriptionField.getText();
        String amountText = amountField.getText();
        String type = (String) typeComboBox.getSelectedItem();

        if (description.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Keterangan dan Nominal harus diisi!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int amount = Integer.parseInt(amountText);
            if (amount <= 0) {
                throw new NumberFormatException();
            }

            if ("Pemasukan".equals(type)) {
                balance += amount;
            } else {
                balance -= amount;
            }

            tableModel.addRow(new Object[]{description, "Rp " + amount, type, null});

            balanceLabel.setText("Total Saldo: Rp " + balance);

            descriptionField.setText("");
            amountField.setText("");
            typeComboBox.setSelectedIndex(0);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Nominal harus berupa angka positif!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow != -1) {
            String type = (String) tableModel.getValueAt(selectedRow, 2);
            String amountText = (String) tableModel.getValueAt(selectedRow, 1);
            int amount = Integer.parseInt(amountText.replace("Rp ", "").replace(",", ""));

            if ("Pemasukan".equals(type)) {
                balance -= amount;
            } else {
                balance += amount;
            }

            tableModel.removeRow(selectedRow);

            balanceLabel.setText("Total Saldo: Rp " + balance);
        } else {
            JOptionPane.showMessageDialog(frame, "Pilih transaksi yang akan dihapus!", "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(selectedFile.getAbsolutePath()).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.setValueAt(imageIcon, selectedRow, 3);
                JOptionPane.showMessageDialog(frame, "Gambar berhasil diunggah.", "Upload Gambar", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Pilih transaksi terlebih dahulu untuk mengunggah gambar!", "Upload Gambar", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FinanceManagerGUI();
            }
        });
    }
}
