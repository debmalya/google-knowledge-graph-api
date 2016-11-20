package org.deb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;

/**
 * Hello world!
 *
 */
public class App {
	public static Properties properties = new Properties();

	public static void main(String[] args) {
		try (Scanner in = new Scanner(System.in)) {
			properties.load(new FileInputStream(
					"./src/main/resource/kgsearch.properties"));
			String apiKey = properties.getProperty("API_KEY");
			if (apiKey == null || "".equals(apiKey)) {
				System.err
						.println("In '"
								+ System.getProperty("user.dir")
								+ "/src/main/resource/kgsearch.properties' insert your API_KEY");
			} else {
				HttpTransport httpTransport = new NetHttpTransport();
				HttpRequestFactory requestFactory = httpTransport
						.createRequestFactory();
				JSONParser parser = new JSONParser();

				entitySearch(in, requestFactory, parser);
			}
		} catch (FileNotFoundException e) {
			System.err.println("'" + System.getProperty("user.dir")
					+ "/src/main/resource/kgsearch.properties' not found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void entitySearch(Scanner in,
			HttpRequestFactory requestFactory, JSONParser parser,
			String... entities) throws IOException, ParseException {
		GenericUrl url = new GenericUrl(
				"https://kgsearch.googleapis.com/v1/entities:search");
		System.out.println("Enter entity name :");
		if (entities == null || entities.length == 0) {
			entities = new String[] { in.nextLine() };
		}
		for (String eachEntity : entities) {
			url.put("query", eachEntity);
			url.put("limit", "10");
			url.put("indent", "true");
			url.put("key", properties.get("API_KEY"));
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			JSONObject response = (JSONObject) parser.parse(httpResponse
					.parseAsString());
			JSONArray elements = (JSONArray) response.get("itemListElement");
			if (elements != null && !elements.isEmpty()) {
				for (Object element : elements) {
					System.out.println(JsonPath.read(element, "$.result.name")
							.toString());
				}
			} else {
				System.err.println("'"+eachEntity+"' not found in the knowledge graph.");
			}
		}
	}
}
