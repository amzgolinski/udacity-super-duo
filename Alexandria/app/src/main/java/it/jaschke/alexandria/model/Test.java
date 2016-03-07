package it.jaschke.alexandria.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

/**
 * Created by azgolinski on 3/6/16.
 */
public class Test {

  public static void main(String[] args) {
    String testData = "{\n" +
        "   \"kind\":\"books#volumes\",\n" +
        "   \"totalItems\":1,\n" +
        "   \"items\":[\n" +
        "      {\n" +
        "         \"kind\":\"books#volume\",\n" +
        "         \"id\":\"tdY_AQAAMAAJ\",\n" +
        "         \"etag\":\"HV5Dw511ODc\",\n" +
        "         \"selfLink\":\"https://www.googleapis.com/books/v1/volumes/tdY_AQAAMAAJ\",\n" +
        "         \"volumeInfo\":{\n" +
        "            \"title\":\"The Confidence-man: His Masquerade\",\n" +
        "            \"subtitle\":\"An Authoritative Text, Backgrounds and Sources, Reviews, Criticism [and] an Annotated Bibliography\",\n" +
        "            \"authors\":[\n" +
        "               \"Herman Melville\"\n" +
        "            ],\n" +
        "            \"publisher\":\"New York : Norton\",\n" +
        "            \"publishedDate\":\"1971\",\n" +
        "            \"description\":\"Set on a Mississippi steamer on April Fool's Day and populated by a series of shape-shifting con men, The Confidence-Man is a challenging metaphysical and ethical exploration of antebellum American society.\",\n" +
        "            \"industryIdentifiers\":[\n" +
        "               {\n" +
        "                  \"type\":\"OTHER\",\n" +
        "                  \"identifier\":\"STANFORD:36105005698464\"\n" +
        "               }\n" +
        "            ],\n" +
        "            \"readingModes\":{\n" +
        "               \"text\":false,\n" +
        "               \"image\":false\n" +
        "            },\n" +
        "            \"pageCount\":376,\n" +
        "            \"printType\":\"BOOK\",\n" +
        "            \"categories\":[\n" +
        "               \"Mississippi River\"\n" +
        "            ],\n" +
        "            \"maturityRating\":\"NOT_MATURE\",\n" +
        "            \"allowAnonLogging\":false,\n" +
        "            \"contentVersion\":\"preview-1.0.0\",\n" +
        "            \"imageLinks\":{\n" +
        "               \"smallThumbnail\":\"http://books.google.com/books/content?id=tdY_AQAAMAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api\",\n" +
        "               \"thumbnail\":\"http://books.google.com/books/content?id=tdY_AQAAMAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api\"\n" +
        "            },\n" +
        "            \"language\":\"en\",\n" +
        "            \"previewLink\":\"http://books.google.com/books?id=tdY_AQAAMAAJ&dq=isbn:9780393099683&hl=&cd=1&source=gbs_api\",\n" +
        "            \"infoLink\":\"http://books.google.com/books?id=tdY_AQAAMAAJ&dq=isbn:9780393099683&hl=&source=gbs_api\",\n" +
        "            \"canonicalVolumeLink\":\"http://books.google.com/books/about/The_Confidence_man_His_Masquerade.html?hl=&id=tdY_AQAAMAAJ\"\n" +
        "         },\n" +
        "         \"saleInfo\":{\n" +
        "            \"country\":\"US\",\n" +
        "            \"saleability\":\"NOT_FOR_SALE\",\n" +
        "            \"isEbook\":false\n" +
        "         },\n" +
        "         \"accessInfo\":{\n" +
        "            \"country\":\"US\",\n" +
        "            \"viewability\":\"NO_PAGES\",\n" +
        "            \"embeddable\":false,\n" +
        "            \"publicDomain\":false,\n" +
        "            \"textToSpeechPermission\":\"ALLOWED\",\n" +
        "            \"epub\":{\n" +
        "               \"isAvailable\":false\n" +
        "            },\n" +
        "            \"pdf\":{\n" +
        "               \"isAvailable\":false\n" +
        "            },\n" +
        "            \"webReaderLink\":\"http://books.google.com/books/reader?id=tdY_AQAAMAAJ&hl=&printsec=frontcover&output=reader&source=gbs_api\",\n" +
        "            \"accessViewStatus\":\"NONE\",\n" +
        "            \"quoteSharingAllowed\":false\n" +
        "         },\n" +
        "         \"searchInfo\":{\n" +
        "            \"textSnippet\":\"Set on a Mississippi steamer on April Fool&#39;s Day and populated by a series of shape-shifting con men, The Confidence-Man is a challenging metaphysical and ethical exploration of antebellum American society.\"\n" +
        "         }\n" +
        "      }\n" +
        "   ]\n" +
        "}";
    Book book;
    final String ITEMS = "items";
    Gson gson = new Gson();
    JsonParser parser = new JsonParser();
    JsonObject object = parser.parse(testData).getAsJsonObject();
    System.out.println(object.toString());
    System.out.println("-------------------------------");
    System.out.println(object.get(ITEMS));
    System.out.println("-------------------------------");
    JsonArray array = object.getAsJsonArray(ITEMS);
    System.out.println(array.toString());
    System.out.println("-------------------------------");
    JsonObject entry = array.get(0).getAsJsonObject();
    System.out.println(entry.toString());
    System.out.println("-------------------------------");

    book = gson.fromJson(entry.get("volumeInfo"), Book.class);

    if (book != null) System.out.println(book.toString());
  }
}
