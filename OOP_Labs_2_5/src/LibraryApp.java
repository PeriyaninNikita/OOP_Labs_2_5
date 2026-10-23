import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Графическое приложение «Библиотека» для лабораторных работ № 2–5:
 * интерфейс, обработка событий, исключений, загрузка и сохранение данных.
 */
public class LibraryApp extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Автор", "Книга", "Год издания", "Наличие"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return true; }
    };
    private final JTable table = new JTable(model);
    private final JTextField authorField = new JTextField(13);
    private final JTextField titleField = new JTextField(13);
    private final JTextField yearField = new JTextField(5);
    private final JComboBox<String> availabilityBox = new JComboBox<>(new String[]{"Есть", "Нет"});
    private final JTextField searchField = new JTextField(14);
    private final JComboBox<String> searchBy = new JComboBox<>(new String[]{"Автор", "Книга", "Год издания", "Наличие"});
    private final JTextArea details = new JTextArea(7, 22);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private File currentFile;

    public LibraryApp() {
        super("Библиотека — лабораторные работы № 2–5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 520));
        setLocationRelativeTo(null);
        buildMenu();
        buildInterface();
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addSampleData();
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelectedBook();
        });
    }

    /** Создает меню, панель действий, таблицу и форму ввода. */
    private void buildInterface() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        addButton(toolbar, "Новая запись", e -> clearFields());
        addButton(toolbar, "Добавить", e -> addBook());
        addButton(toolbar, "Изменить", e -> updateBook());
        addButton(toolbar, "Удалить", e -> deleteBook());
        addButton(toolbar, "Открыть", e -> openFile());
        addButton(toolbar, "Сохранить", e -> saveFile());
        add(toolbar, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Поиск"));
        searchPanel.add(new JLabel("Искать по:"));
        searchPanel.add(searchBy);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Найти");
        searchButton.addActionListener(e -> searchBooks());
        searchPanel.add(searchButton);
        JButton resetButton = new JButton("Сбросить");
        resetButton.addActionListener(e -> { searchField.setText(""); sorter.setRowFilter(null); });
        searchPanel.add(resetButton);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Карточка книги"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5); g.anchor = GridBagConstraints.WEST;
        addFormRow(form, g, 0, "Автор:", authorField);
        addFormRow(form, g, 1, "Название:", titleField);
        addFormRow(form, g, 2, "Год издания:", yearField);
        addFormRow(form, g, 3, "Наличие:", availabilityBox);
        JButton clear = new JButton("Очистить поля");
        clear.addActionListener(e -> clearFields());
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2; form.add(clear, g);

        details.setEditable(false);
        details.setLineWrap(true); details.setWrapStyleWord(true);
        details.setText("Выберите строку таблицы, чтобы увидеть карточку книги.");
        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.add(form, BorderLayout.NORTH);
        right.add(new JScrollPane(details), BorderLayout.CENTER);
        right.setPreferredSize(new Dimension(300, 300));

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.add(searchPanel, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(right, BorderLayout.EAST);
        center.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        add(center, BorderLayout.CENTER);
        setJMenuBar(createMenuBar());
    }

    private void addFormRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridwidth = 1; g.gridx = 0; g.gridy = row; panel.add(new JLabel(label), g);
        g.gridx = 1; panel.add(field, g);
    }

    private void addButton(JToolBar bar, String title, ActionListener action) {
        JButton button = new JButton(title);
        button.addActionListener(action);
        bar.add(button);
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("Файл");
        JMenuItem newItem = new JMenuItem("Новая запись");
        newItem.addActionListener(e -> clearFields());
        JMenuItem open = new JMenuItem("Открыть...");
        open.addActionListener(e -> openFile());
        JMenuItem save = new JMenuItem("Сохранить...");
        save.addActionListener(e -> saveFile());
        JMenuItem exit = new JMenuItem("Выход");
        exit.addActionListener(e -> dispose());
        file.add(newItem); file.add(open); file.add(save); file.addSeparator(); file.add(exit);
        bar.add(file);
        return bar;
    }

    private void buildMenu() { /* Меню устанавливается в buildInterface(). */ }

    private void addSampleData() {
        model.addRow(new Object[]{"Александр Дюма", "Три мушкетёра", "1844", "Есть"});
        model.addRow(new Object[]{"Алексей Толстой", "Анна Каренина", "1877", "Нет"});
        model.addRow(new Object[]{"Михаил Булгаков", "Мастер и Маргарита", "1967", "Есть"});
    }

    private Book readForm() {
        return new Book(authorField.getText(), titleField.getText(), yearField.getText(),
                String.valueOf(availabilityBox.getSelectedItem()));
    }

    private void addBook() {
        try {
            Book book = readForm();
            model.addRow(book.toRow());
            clearFields();
            JOptionPane.showMessageDialog(this, "Книга добавлена.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateBook() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { showError("Сначала выберите строку таблицы."); return; }
        try {
            Book book = readForm();
            int row = table.convertRowIndexToModel(viewRow);
            for (int c = 0; c < 4; c++) model.setValueAt(book.toRow()[c], row, c);
            JOptionPane.showMessageDialog(this, "Данные книги изменены.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Сначала выберите книгу для удаления."); return; }
        int answer = JOptionPane.showConfirmDialog(this, "Удалить выбранную книгу?", "Подтверждение",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) model.removeRow(table.convertRowIndexToModel(row));
    }

    private void searchBooks() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { sorter.setRowFilter(null); return; }
        int column = searchBy.getSelectedIndex();
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), column));
        if (table.getRowCount() == 0) JOptionPane.showMessageDialog(this, "Ничего не найдено.");
    }

    private void showSelectedBook() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);
        details.setText("Автор: " + model.getValueAt(row, 0) + "\nНазвание: " + model.getValueAt(row, 1)
                + "\nГод издания: " + model.getValueAt(row, 2) + "\nНаличие: " + model.getValueAt(row, 3));
        authorField.setText(String.valueOf(model.getValueAt(row, 0)));
        titleField.setText(String.valueOf(model.getValueAt(row, 1)));
        yearField.setText(String.valueOf(model.getValueAt(row, 2)));
        availabilityBox.setSelectedItem(String.valueOf(model.getValueAt(row, 3)));
    }

    private void clearFields() {
        table.clearSelection();
        authorField.setText(""); titleField.setText(""); yearField.setText("");
        availabilityBox.setSelectedItem("Есть");
        details.setText("Введите данные книги или выберите строку таблицы.");
        authorField.requestFocusInWindow();
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Открыть список книг");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            LibraryIO.load(chooser.getSelectedFile(), model);
            currentFile = chooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Загружено записей: " + model.getRowCount());
        } catch (java.io.FileNotFoundException ex) {
            showError("Файл не найден: " + ex.getMessage());
        } catch (LibraryException ex) {
            showError("Некорректные данные в файле: " + ex.getMessage());
        } catch (java.io.IOException ex) {
            showError("Ошибка чтения файла: " + ex.getMessage());
        } catch (SecurityException ex) {
            showError("Нет разрешения на чтение файла.");
        }
    }

    private void saveFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Сохранить список книг");
        if (currentFile != null) chooser.setSelectedFile(currentFile);
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        try {
            LibraryIO.save(file, model);
            currentFile = file;
            JOptionPane.showMessageDialog(this, "Сохранено записей: " + model.getRowCount());
        } catch (java.io.IOException ex) {
            showError("Ошибка записи файла: " + ex.getMessage());
        } catch (SecurityException ex) {
            showError("Нет разрешения на запись файла.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryApp().setVisible(true));
    }
}