package stepDefinitions;

import PojoClasses.AddPlace;
import PojoClasses.Location;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class placeStepDefs {
    private RequestSpecification res;
    private ResponseSpecification resSpec;
    private Response resp;

    @Given("^Add Place Payload$")
    public void add_Place_Payload() throws Throwable {
        AddPlace p = new AddPlace();
        p.setAccuracy(50);
        p.setAddress("29, side layout, cohen t63432");
        p.setLanguage("Tamil-IN");
        p.setPhoneNumber("+563959230520");
        p.setName("Raheja Arcade");
        p.setWebsite("https://github.com/svcerahula");
        List<String> myList =new ArrayList<String>();
        myList.add("shoe park");
        myList.add("shop");

        p.setTypes(myList);
        Location l =new Location();
        l.setLat(-38.383494);
        l.setLng(33.427362);
        p.setLocation(l);
        RequestSpecification req = new RequestSpecBuilder().setBaseUri("https://www.rahulshettyacademy.com")
                .setContentType(ContentType.JSON)
                .addQueryParam("key","qaclick175")
                .build();

        resSpec = new ResponseSpecBuilder().expectStatusCode(200).expectContentType(ContentType.JSON)
                .build();

        res = given().spec(req).body(p);
    }

    @When("^user calls \"([^\"]*)\" with Post http request$")
    public void user_calls_with_Post_http_request(String arg1) throws Throwable {
        resp = res.when().post("/maps/api/place/add/json").then().spec(resSpec).extract().response();
    }

    @Then("^the API call is successful with status code (\\d+)$")
    public void the_API_call_is_successful_with_status_code(int expectedStatusCode) throws Throwable {
        Assert.assertEquals(resp.getStatusCode(),expectedStatusCode);
    }

    @Then("^\"([^\"]*)\" is response body should be \"([^\"]*)\"$")
    public void is_response_body_should_be(String attr, String value) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        String respString = resp.asString();
        // some issue with the app
        //it throws Undefined property: stdClass::$phone_number in <b>/var/www/maps/api/place/AddPlace.php error
        //hence performing the below string manipulation
        respString = respString.replaceAll("<br \\/>\\n+.*<br \\/>","");
        System.out.println("response output : "+respString);
        JsonPath js = new JsonPath(respString);
        System.out.println("Json output : "+  js.prettyPrint());
        Assert.assertEquals(js.get(attr).toString(),value);
    }
}
