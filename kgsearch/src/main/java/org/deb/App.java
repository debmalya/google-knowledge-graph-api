package org.deb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
		FileInputStream fis = null;
		try (Scanner in = new Scanner(System.in)) {
			fis = new FileInputStream("./src/main/resource/kgsearch.properties");
			properties.load(fis);

			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			JSONParser parser = new JSONParser();

			App app = new App();
			Response response = app.entitySearch(in, requestFactory, parser, properties);
			System.out.println(response);

		} catch (FileNotFoundException e) {
			System.err.println(
					"ERR: '" + System.getProperty("user.dir") + "/src/main/resource/kgsearch.properties' not found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ignore) {

				}
			}
		}

	}

	public Response entitySearch(Scanner in, HttpRequestFactory requestFactory, JSONParser parser,
			Properties properties, String... entities) throws IOException, ParseException {
		String apiKey = properties.getProperty("API_KEY");
		Response kgSearchResponse = new Response();
		List<String> responseList = new ArrayList<>();
		List<String> imageList = new ArrayList<>();
		List<String> urlList = new ArrayList<>();
		if (apiKey == null || "".equals(apiKey)) {
			System.err.println("ERR: In '" + System.getProperty("user.dir")
					+ "/src/main/resource/kgsearch.properties' insert your API_KEY");

			responseList.add("ERR: In '" + System.getProperty("user.dir")
					+ "/src/main/resource/kgsearch.properties' insert your API_KEY");

		} else {
			GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");

			if (entities == null || entities.length == 0) {
				System.out.println("Enter entity name :");
				entities = new String[] { in.nextLine() };
				System.out.println("Please wait, searching ...");
			}

			for (String eachEntity : entities) {
				url.put("query", eachEntity);
				url.put("limit", "10");
				url.put("indent", "true");
				url.put("key", apiKey);
				HttpRequest request = requestFactory.buildGetRequest(url);
				HttpResponse httpResponse = request.execute();
				JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
				JSONArray elements = (JSONArray) response.get("itemListElement");
				 
				if (elements != null && !elements.isEmpty()) {
					for (Object element : elements) {

						try {
							responseList
									.add(JsonPath.read(element, "$.result.detailedDescription.articleBody").toString());
							urlList.add(JsonPath.read(element, "$.result.detailedDescription.url").toString());
//							imageList.add(JsonPath.read(element, "$.result.image.url").toString());
						} catch (Throwable ignore) {
						}

					}
				} else {
					responseList.add("'" + eachEntity + "' not found in the knowledge graph.");
				}
			}
		}

		kgSearchResponse.setResultList(responseList);
		kgSearchResponse.setUrls(urlList);
		return kgSearchResponse;
	}
}
