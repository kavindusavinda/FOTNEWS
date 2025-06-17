package com.example.fotnews;

public class News {
    private String title;
    private String description;
    private String category;
    private String imageUrl;

    public News() {
        // Required empty constructor for Firebase
    }

    public News(String title, String description, String category, String imageUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }


}
