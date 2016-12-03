package org.deb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.BeforeClass;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	public static Properties properties = new Properties();

	HttpTransport httpTransport = new NetHttpTransport();
	HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
	JSONParser parser = new JSONParser();

	@BeforeClass
	public static void init() {
		
	}

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("./src/main/resource/kgsearch.properties");
			properties.load(fis);
			Response response = new App().entitySearch(null, requestFactory, parser, properties, 10, true,
					null, new String[] { "Amitabh Bacchan" });
			Assert.assertNotNull(response);
			Assert.assertTrue(response.getResultList().size() > 0);
			Assert.assertTrue(response.getResultList().size() < 11);
			for (String eachResultSet : response.getResultList()) {
				eachResultSet = eachResultSet.toLowerCase();
				Assert.assertTrue(
						"Every search result should contain the word Amitabh Bachchan, but '" + eachResultSet
								+ "' does not follow.",
						eachResultSet.toLowerCase().contains("amitabh")
								|| eachResultSet.toLowerCase().contains("bacchan"));
				System.out.println(eachResultSet);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			Assert.assertFalse(e.getMessage(), true);
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
	 * Rigourous Test :-)
	 */
	public void testAppNonExactMatch() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("./src/main/resource/kgsearch.properties");
			properties.load(fis);
			Response response = new App().entitySearch(null, requestFactory, parser, properties, 10, false, null,
					new String[] { "Amitabh Bacchan" });
			Assert.assertNotNull(response);
			Assert.assertTrue(response.getResultList().size() == 10);

		} catch (IOException | ParseException e) {
			e.printStackTrace();
			Assert.assertFalse(e.getMessage(), true);
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
	 * Rigourous Test :-)
	 */
	public void testAppNonExistingEntity() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("./src/main/resource/kgsearch.properties");
			properties.load(fis);
			Response response = new App().entitySearch(null, requestFactory, parser, properties, 10, false,
					null, new String[] { "Debmalya" });
			Assert.assertNotNull(response);
			Assert.assertTrue(response.getResultList().size() == 1);
			Assert.assertTrue(response.getResultList().size() < 11);
			Assert.assertEquals("'[Debmalya]' not found in the knowledge graph.", response.getResultList().get(0));

		} catch (IOException | ParseException e) {
			e.printStackTrace();
			Assert.assertFalse(e.getMessage(), true);
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
	 * Rigourous Test :-)
	 */
	public void testAppForPerson() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("./src/main/resource/kgsearch.properties");
			properties.load(fis);
			Response response = new App().entitySearch(null, requestFactory, parser, properties, 10, false,
					App.PERSON_SEARCH_URL, new String[] { "Aristotle" });
			Assert.assertNotNull(response);
			Assert.assertTrue(response.getResultList().size() == 10);
			

		} catch (IOException | ParseException e) {
			e.printStackTrace();
			Assert.assertFalse(e.getMessage(), true);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ignore) {

				}
			}
		}
	}

}
