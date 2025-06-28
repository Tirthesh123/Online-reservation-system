public class User {
    private int pnr;
    private String username;
    private String password; // previously passwordHash
    private String role;

    public User(int pnr, String username, String password, String role) {
        this.pnr = pnr;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public String getRole() { return role; }

}
