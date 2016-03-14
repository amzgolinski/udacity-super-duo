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

    // title
    sb.append("Title: ");
    if (title != null ) sb.append(title);
    sb.append("\n");

    // subtitle
    sb.append("Subtitle: ");
    if (subtitle != null) sb.append(subtitle);
    sb.append("\n");

    // description
    sb.append("Description: ");
    if (description != null) sb.append(description);
    sb.append("\n");

    // authors
    sb.append("Authors: ");
    if (authors != null) {
      for (String author : authors) {
        sb.append(author + " ");
      }
    }
    sb.append("\n");

    // categories
    sb.append("Categories: ");
    if (categories != null) {
      for (String cat : categories) {
        sb.append(cat + " ");
      }
    }
    sb.append("\n");

    // thumbnail
    sb.append("Thumbnail: ");
    if (imageLinks !=  null) sb.append(imageLinks.thumbnail);

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
    String thumbnail = null;
    if (imageLinks != null) {
      thumbnail = imageLinks.thumbnail;
    }
    return thumbnail;
  }

  public boolean hasAuthors() {
    return (authors != null && authors.length > 0);
  }

  public boolean hasCategories() {
    return (categories != null && categories.length > 0);
  }

}
