package kr.ac.hs.farm;

public class RegisterRequest {
    private String id;
    private String password;

    public RegisterRequest(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
