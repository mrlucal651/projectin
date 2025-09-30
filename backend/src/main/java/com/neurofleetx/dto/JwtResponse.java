package com.neurofleetx.dto;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String company;
    private String userType;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String email, String firstName, String lastName, String company, String userType, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.userType = userType;
        this.roles = roles;
    }

    // Getters and Setters
    public String getAccessToken() { return token; }
    public void setAccessToken(String accessToken) { this.token = accessToken; }

    public String getTokenType() { return type; }
    public void setTokenType(String tokenType) { this.type = tokenType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}