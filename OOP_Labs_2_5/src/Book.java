/**
 * Книга библиотечного каталога.
 * @author Student
 */
public class Book {
    private String author;
    private String title;
    private String year;
    private String availability;

    public Book(String author, String title, String year, String availability) {
        setAuthor(author);
        setTitle(title);
        setYear(year);
        setAvailability(availability);
    }

    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public String getYear() { return year; }
    public String getAvailability() { return availability; }

    public void setAuthor(String author) { this.author = clean(author, "Автор"); }
    public void setTitle(String title) { this.title = clean(title, "Название"); }
    public void setYear(String year) {
        String value = clean(year, "Год издания");
        if (!value.matches("\\d{4}")) throw new IllegalArgumentException("Год должен состоять из 4 цифр.");
        this.year = value;
    }
    public void setAvailability(String availability) {
        String value = clean(availability, "Наличие");
        if (!value.equalsIgnoreCase("Есть") && !value.equalsIgnoreCase("Нет"))
            throw new IllegalArgumentException("Наличие должно быть указано как «Есть» или «Нет».");
        this.availability = value;
    }

    private String clean(String value, String field) {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException("Поле «" + field + "» не должно быть пустым.");
        return value.trim();
    }

    public String[] toRow() { return new String[]{author, title, year, availability}; }
}