package org.deb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	/**
	 * 
	 */
	public static final String ENTITY_SEARCH_URL = "https://kgsearch.googleapis.com/v1/entities:search";
	public static final String PERSON_SEARCH_URL = "https://kgsearch.googleapis.com/v1/person:search";
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
			int limit = 1;
			try {
				if (args.length == 2) {
					limit = Integer.parseInt(args[1]);
					if (limit > 10) {
						limit = 10;
					} else if (limit < 0) {
						limit = 1;
					}
				}

			} catch (NumberFormatException neverMind) {

			}
			Response response = app.entitySearch(in, requestFactory, parser, properties, limit);
			System.out.println(response);
			System.out.println("Total number of elements :" + response.getResultList().size());

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

	/**
	 * 
	 * @param in
	 *            - InputStream to accept the parameters.
	 * @param requestFactory
	 *            - HttpRequestFactory to make the request.
	 * @param parser
	 *            - To parse JSON response.
	 * @param properties
	 *            - To get the API_KEY
	 * @param limit
	 *            - How many result will be listed (Range is 1 - 10)
	 * @param entities
	 *            - What entities will be searched.
	 * @return Search response.
	 * @throws IOException
	 * @throws ParseException
	 *             - If there is any error while parsing JSON response.
	 */
	public Response entitySearch(Scanner in, HttpRequestFactory requestFactory, JSONParser parser,
			Properties properties, int limit, String... entities) throws IOException, ParseException {
		return entitySearch(in, requestFactory, parser, properties, limit, false, null, entities);
	}

	/**
	 * 
	 * @param in
	 *            - InputStream to accept the parameters.
	 * @param requestFactory
	 *            - HttpRequestFactory to make the request.
	 * @param parser
	 *            - To parse JSON response.
	 * @param properties
	 *            - To get the API_KEY
	 * @param limit
	 *            - How many result will be listed (Range is 1 - 10)
	 * @param whether
	 *            we need to do a exact match or not?
	 * @param searchURL
	 *            - URL to be searched for it can be for entity,person,movie
	 * @param entities
	 *            - What entities will be searched.
	 * @return Search response.
	 * @throws IOException
	 * @throws ParseException
	 *             - If there is any error while parsing JSON response.
	 */
	public Response entitySearch(Scanner in, HttpRequestFactory requestFactory, JSONParser parser,
			Properties properties, int limit, boolean isExactMatch, String searchURL, String... entities)
			throws IOException, ParseException {
		System.out.println("Default Search limit :" + limit);
		int acceptedLimit = limit;
		String apiKey = properties.getProperty("API_KEY");
		Response kgSearchResponse = new Response();
		
		List<String> responseList = new ArrayList<>();
		List<String[]> typeList = new ArrayList<>();
		List<String> urlList = new ArrayList<>();
		
		StringBuilder typeBuilder = new StringBuilder();
		
		if (apiKey == null || "".equals(apiKey)) {
			System.err.println("ERR: In '" + System.getProperty("user.dir")
					+ "/src/main/resource/kgsearch.properties' insert your API_KEY");

			responseList.add("ERR: In '" + System.getProperty("user.dir")
					+ "/src/main/resource/kgsearch.properties' insert your API_KEY");

		} else {
			GenericUrl url = new GenericUrl(ENTITY_SEARCH_URL);
			

			if (entities == null || entities.length == 0) {
				System.out.println("Please enter entity name :");
				entities = new String[] { in.nextLine() };
				System.out.println("Please enter search limit (1 - 10):");
				try {
					acceptedLimit = Integer.parseInt(in.nextLine());
					if (acceptedLimit < -1 || acceptedLimit > 10) {
						acceptedLimit = limit;
					}
				} catch (NumberFormatException neverMind) {

				}
				System.out.println("Please wait, searching ...");
			}

			for (String eachEntity : entities) {
				url.put("query", eachEntity);
				url.put("limit", acceptedLimit);
				url.put("indent", "true");
				url.put("key", apiKey);
				HttpRequest request = requestFactory.buildGetRequest(url);
				HttpResponse httpResponse = request.execute();
				
				JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
				JSONArray elements = (JSONArray) response.get("itemListElement");

				if (elements != null && !elements.isEmpty()) {
					for (Object element : elements) {
						
						try {
							
								String searchResult = JsonPath.read(element, "$.result.detailedDescription.articleBody")
										.toString();
								if (isExactMatch) {
									if (searchResult.contains(eachEntity)) {

										responseList.add(searchResult);
										urlList.add(
												JsonPath.read(element, "$.result.detailedDescription.url").toString());
										// imageList.add(JsonPath.read(element,
										// "$.result.image.url").toString());

									}
								} else {
									JSONArray types = JsonPath.read(element, "$.result.@type");
									
									for (int i = 0; i < types.size(); i++){
										typeBuilder.append( types.get(i).toString() );
										typeBuilder.append(" , ");
									}
									
									responseList.add(searchResult);
									urlList.add(JsonPath.read(element, "$.result.detailedDescription.url").toString());
								}
							
						} catch (Throwable ignore) {
//							ignore.printStackTrace();
						}

					}
				} else {
					String[] allEntities = eachEntity.split(" ");
					if (allEntities.length > 0) {
						System.out.println("Problem here ");
						return entitySearch(in, requestFactory, parser, properties, limit, allEntities);
					}
				}
			}
		}

		if (responseList.isEmpty()) {
			responseList.add("'" + Arrays.toString(entities) + "' not found in the knowledge graph.");
		}
		kgSearchResponse.setResultList(responseList);
		kgSearchResponse.setUrls(urlList);
		kgSearchResponse.setType(typeBuilder.toString());
		return kgSearchResponse;
	}
}
