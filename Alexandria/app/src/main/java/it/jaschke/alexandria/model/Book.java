package it.jaschke.alexandria.model;


public class Book {

  private String title;
  private String subtitle;
  private String[] authors;
  private String[] categories;
  private String description;
  private Links imageLinks;

  static class Links {
    String smallThumbnail;
    String thumbnail;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Title: " + title + "\n");
    sb.append("Subtitle: " + subtitle + "\n");
    sb.append("Description: " + description + "\n");

    if (authors != null) {
      sb.append("Authors: ");
      for (String author : authors) {
        sb.append(author + " ");
      }
      sb.append("\n");
    }
    if (categories != null) {
      sb.append("Categories: ");
      for (String cat : categories) {
        sb.append(cat + " ");
      }
    }
    sb.append("\n");
    sb.append("Thumbnail: " + imageLinks.thumbnail + "\n");
    return sb.toString();
  }

  public String getTitle() {
    return title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public String getDescription() {
    return description;
  }

  public String[] getAuthors() {
    return authors;
  }

  public String[] getCategories() {
    return categories;
  }

  public String getThumbnail() {
    return imageLinks.thumbnail;
  }

  public boolean hasAuthors() {
    return (authors.length > 0);
  }

  public boolean hasCategories() {
    return (categories.length > 0);
  }

}
