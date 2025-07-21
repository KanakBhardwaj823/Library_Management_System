import java.util.*;

class Book {
    private int id;
    private String title;
    private String author;
    private boolean isIssued;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isIssued = false;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isIssued() { return isIssued; }

    public void issue() { isIssued = true; }
    public void returnBook() { isIssued = false; }
}

class User {
    private int id;
    private String name;
    private List<String> borrowingHistory;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
        this.borrowingHistory = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public void addBorrowingRecord(String record) {
        borrowingHistory.add(record);
    }

    public void showBorrowingHistory() {
        System.out.print("Borrowing History for " + name + ": ");
        if (borrowingHistory.isEmpty()) {
            System.out.println("No records found.");
        } else {
            System.out.println();
            for (String record : borrowingHistory) {
                System.out.println("- " + record);
            }
        }
    }
}

class Library {
    private Map<Integer, Book> books;
    private Map<Integer, User> users;
    private Map<Integer, Integer> issuedBooks; // Book ID -> User ID

    public Library() {
        books = new HashMap<>();
        users = new HashMap<>();
        issuedBooks = new HashMap<>();
    }

    public void addBook(Book book) {
        if (book.getId() <= 0) {
            System.out.println("Invalid book ID. Must be positive.");
            return;
        }
        if (books.containsKey(book.getId())) {
            System.out.println("Book already exists! Book not added.");
            return;
        }
        books.put(book.getId(), book);
        System.out.println("Book added: " + book.getTitle());
    }

    public void removeBook(int bookId) {
        if (books.containsKey(bookId)) {
            Book book = books.get(bookId);
            books.remove(bookId);
            System.out.println(book.getTitle() + "book removed.");
            return;
        }
        System.out.println("Book not found.");
    }

    public void addUser(User user) {
        if (user.getId() <= 0) {
            System.out.println("Invalid user ID. Must be positive.");
            return;
        }
        if(userExists(user.getId())) {
            System.out.println("User already exists! User not added.");
            return;
        }
        users.put(user.getId(), user);
        System.out.println("User added: " + user.getName());
    }

    public void issueBook(int bookId, int userId) {
        Book book = findBookById(bookId);
        User user = users.get(userId);
        if (!canIssueMore(userId)) {
            System.out.println("User already issused 3 books! More books cannot be issue.");
            return;
        }
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        if (book.isIssued()) {
            System.out.println("Book already issued.");
            return;
        }

        book.issue();
        issuedBooks.put(bookId, userId);
        user.addBorrowingRecord("Issued: " + book.getTitle());
        System.out.println("Book issued: " + book.getTitle() + " to " + user.getName());
    }

    public boolean canIssueMore(int userId) {
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : issuedBooks.entrySet()) {
            if (entry.getValue() == userId) count++;
        }
        return count < 3;
    }

    public void returnBook(int bookId, int userId) {
        Book book = findBookById(bookId);
        User user = users.get(userId);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        if (!book.isIssued()) {
            System.out.println("Book is not issued.");
            return;
        }
        if (issuedBooks.get(bookId) != userId) {
            System.out.println("This user did not issue the book.");
            return;
        }

        book.returnBook();
        issuedBooks.remove(bookId);
        user.addBorrowingRecord("Returned: " + book.getTitle());
        System.out.println("Book returned: " + book.getTitle() + " by " + user.getName());
    }

    public void showAllBooks() {
        System.out.println("\nLibrary Inventory:");
        for (Map.Entry<Integer, Book> entry : books.entrySet()) {
            int bookId = entry.getKey();
            Book book = entry.getValue();
            System.out.println(bookId + ": " + book.getTitle() + " by " + book.getAuthor() +(book.isIssued() ? " [Issued]" : " [Available]"));
        }
    }

    public void showUserHistory(int userId) {
        User user = users.get(userId);
        if (user == null) {
            System.out.println("User not found.");
        } else {
            user.showBorrowingHistory();
        }
    }

    private Book findBookById(int bookId) {
        return books.get(bookId);
    }

    public boolean userExists(int userId) {
        return users.containsKey(userId);
    }

    public boolean bookExists(int bookId) {
        return findBookById(bookId) != null;
    }
}

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library library = new Library();

        // Sample Data
        library.addBook(new Book(1, "Java Programming", "James Gosling"));
        library.addBook(new Book(2, "Effective Java", "Joshua Bloch"));
        library.addBook(new Book(3, "Python Basics", "Guido van Rossum"));

        library.addUser(new User(1, "Alice"));
        library.addUser(new User(2, "Bob"));

        boolean running = true;
        while (running) {
            System.out.println("\n===== Library Menu =====");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Show All Books");
            System.out.println("6. Add User");
            System.out.println("7. Show User Borrowing History");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            int choice = safeNextInt(sc);

            switch (choice) {
                case 1:
                    System.out.print("Enter Book ID: ");
                    int newId = safeNextInt(sc);
                    if (library.bookExists(newId)) {
                        System.out.println("Book ID already exists. Book cannot be added");
                        break;
                    }
                    sc.nextLine(); // consume newline
                    System.out.print("Enter Book Title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter Author: ");
                    String author = sc.nextLine();
                    library.addBook(new Book(newId, title, author));
                    break;
                case 2:
                    System.out.print("Enter Book ID to Remove: ");
                    int removeId = safeNextInt(sc);
                    library.removeBook(removeId);
                    break;
                case 3:
                    System.out.print("Enter Book ID to Issue: ");
                    int issueBookId = safeNextInt(sc);
                    System.out.print("Enter User ID: ");
                    int issueUserId = safeNextInt(sc);
                    library.issueBook(issueBookId, issueUserId);
                    break;
                case 4:
                    System.out.print("Enter Book ID to Return: ");
                    int returnBookId = safeNextInt(sc);
                    System.out.print("Enter User ID: ");
                    int returnUserId = safeNextInt(sc);
                    library.returnBook(returnBookId, returnUserId);
                    break;
                case 5:
                    library.showAllBooks();
                    break;
                case 6:
                    System.out.print("Enter New User ID: ");
                    int userID = safeNextInt(sc);
                    if (library.userExists(userID)) {
                        System.out.println("User ID already exists. User not added.");
                        break;
                    }
                    sc.nextLine(); // consume newline
                    System.out.print("Enter New User Name: ");
                    String userName = sc.nextLine();
                    library.addUser(new User(userID, userName));
                    break;
                case 7:
                    System.out.print("Enter User ID: ");
                    int userId = safeNextInt(sc);
                    library.showUserHistory(userId);
                    break;
                case 8:
                    running = false;
                    System.out.println("Exiting Library System.");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        sc.close();
    }

    public static int safeNextInt(Scanner sc) {
        while (true) {
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter an integer: ");
                sc.nextLine(); // clear wrong input
            }
        }
    }
}
