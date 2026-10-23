import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.swing.table.DefaultTableModel;

/** Вспомогательные методы загрузки и сохранения табличных данных. */
public final class LibraryIO {
    private LibraryIO() {}

    /** Сохраняет таблицу в текстовый файл: поля разделены символом |. */
    public static void save(File file, DefaultTableModel model) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    String value = String.valueOf(model.getValueAt(r, c));
                    if (value.contains("|") || value.contains("\n") || value.contains("\r"))
                        throw new IOException("Поля не должны содержать символ | или перевод строки.");
                    if (c > 0) writer.write("|");
                    writer.write(value);
                }
                writer.newLine();
            }
        }
    }

    /** Загружает строки из файла и заменяет содержимое модели таблицы. */
    public static void load(File file, DefaultTableModel model) throws IOException, LibraryException {
        DefaultTableModel temp = new DefaultTableModel(
                new Object[]{"Автор", "Книга", "Год издания", "Наличие"}, 0);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length != 4)
                    throw new LibraryException("Строка " + lineNo + ": ожидалось 4 поля, разделённые |.");
                try {
                    Book book = new Book(parts[0], parts[1], parts[2], parts[3]);
                    temp.addRow(book.toRow());
                } catch (IllegalArgumentException ex) {
                    throw new LibraryException("Строка " + lineNo + ": " + ex.getMessage());
                }
            }
        }
        model.setRowCount(0);
        for (int i = 0; i < temp.getRowCount(); i++) {
            model.addRow(new Object[]{temp.getValueAt(i,0), temp.getValueAt(i,1),
                    temp.getValueAt(i,2), temp.getValueAt(i,3)});
        }
    }
}