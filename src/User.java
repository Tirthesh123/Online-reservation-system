public class User {
    private int user_id;
    private String username;
    private String name;
    private String email;
    private String role;

    public User(int user_id, String username, String name, String email, String role) {
        this.user_id = user_id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters and setters
    public int getUser_id() { return user_id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }



}
