package stepDefinitions;

import java.util.List;
import java.util.Map;

import org.junit.Assert;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class CatSteps {
	// move these to env variables
	private static final String FACT_BASE_URL = "https://catfact.ninja/";

	private Response response;


	@Given("Get facts API with length as {string}, page as {string} and limit as {string}")
	public void getFactAPIWithLength(String len, String page, String limit) {
		RestAssured.baseURI = FACT_BASE_URL;
		RequestSpecification request = RestAssured.given();
		response = request.get("facts?max_length="+ len + "&limit="+limit + "&page="+page );
	}

	@Then("status code equals {int}")
	public void statusCodeCheck(Integer code) {
		Assert.assertEquals(code.intValue(), response.getStatusCode());
	}

	@And("Response is valid with length, page and limit as {string},{string},{string}")
	public void responseValidationWithParam(String len, String page, String limit) {
		int lenVal = Integer.parseInt(len);
		int limitVal = Integer.parseInt(limit);
		int pageVal = Integer.parseInt(page);
		int currentPage = getJsonPathAsInt(response, "current_page");
		Assert.assertTrue(currentPage == pageVal);
		List<Map<String, Object>> data = getJsonPathAsList(response, "data");
		Assert.assertTrue(data.size() <= limitVal);
		for (Map<String, Object> factData : data) {
			Assert.assertTrue(Integer.parseInt(factData.get("length").toString()) <= lenVal);
			Assert.assertTrue(factData.get("fact").toString().length() <= lenVal);
			Assert.assertTrue(factData.get("fact").toString().length() == Integer.parseInt(factData.get("length").toString()));
		}
		int total = getJsonPathAsInt(response, "total");
		int numberOfPages = total/limitVal;
		int reminderPage = total % limitVal;
		if (reminderPage > 0){
			numberOfPages = numberOfPages + 1;
		}
		int lastPage = getJsonPathAsInt(response, "last_page");
		Assert.assertTrue(numberOfPages == lastPage);
		int fromActual = getJsonPathAsInt(response, "from");
		int fromExpected = ((pageVal -1) * limitVal) + 1;
		Assert.assertTrue(fromActual == fromExpected);
	}

	public String getJsonPathAsString(Response response, String key) {
		String resp = response.asString();
		JsonPath js = new JsonPath(resp);
		if (js.get(key) == null) {
			return "";
		}
		return js.get(key).toString();
	}

	public int getJsonPathAsInt(Response response, String key) {
		String resp = response.asString();
		JsonPath js = new JsonPath(resp);
		if (js.get(key) == null) {
			return 0;
		}
		return Integer.parseInt(js.get(key).toString());
	}

	public List<Map<String, Object>> getJsonPathAsList(Response response, String key) {
		String resp = response.asString();
		JsonPath js = new JsonPath(resp);
		if (js.get(key) == null) {
			return null;
		}
		List<Map<String, Object>> data = js.get(key);
		return data;
	}

}
