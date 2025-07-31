package kr.ac.hs.farm;

public class RegisterRequest {
    private String id;
    private String password;
    private float weight;
    private String name;

    public RegisterRequest(String id, String password, float weight, String name) {
        this.id = id;
        this.password = password;
        this.weight = weight;
        this.name = name;
    }
}