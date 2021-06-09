package com.example.oxipulse.model;

public class person {
    private String FirstName;
    private String MiddleName;
    private String LastName;
    private String isDoc;
    private String ImageUrl;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getIsDoc() {
        return isDoc;
    }

    public void setIsDoc(String isDoc) {
        this.isDoc = isDoc;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
