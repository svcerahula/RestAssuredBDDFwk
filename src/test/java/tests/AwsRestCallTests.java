package tests;

import Utils.AWSV4Auth;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class AwsRestCallTests {
    private static Properties awsProp = new Properties();
    public String accessKeyId;
    public String region;
    public String dateStamp;
    public String secretAccessKey;
    public String serviceName;

    @BeforeClass
    public void initAwsProperties() throws IOException {
        awsProp = getAWSProperties();
        accessKeyId = awsProp.getProperty("awsAccessKeyId");
        secretAccessKey = awsProp.getProperty("awsSecretAccessKey");
        dateStamp = awsProp.getProperty("dateStamp");
        region = awsProp.getProperty("awsMumbaiRegion");
        serviceName = awsProp.getProperty("awsSQSService");
    }

    @Test
    public void awsSqsRestCall() {
        RequestSpecification restAssuredRequest;
        restAssuredRequest = RestAssured.given();

        TreeMap<String,String> awsHeaders = new TreeMap<String, String>();
        awsHeaders.put("host",awsProp.getProperty("sqsHostName"));
        //awsHeaders.put();

        //set the content type
        restAssuredRequest.contentType(ContentType.URLENC);

        //set the query params for the GET request to sqs aws queue system
        TreeMap<String,String> queryParams = new TreeMap<>();
        queryParams.put("Action","CreateQueue");
        queryParams.put("DefaultVisibilityTimeout","40");
        queryParams.put("QueueName","Myqueue3");
        queryParams.put("Version","2012-11-05");

        AWSV4Auth awsv4 = new AWSV4Auth.Builder(accessKeyId, secretAccessKey)
                .regionName(region)
                .serviceName(serviceName)
                .httpMethodName("GET")
                .queryParametes(queryParams)
                .awsHeaders(awsHeaders).build();

        /*
        get the headers set the AWSV4Auth object and set it in the restAssured given() object
         */
        Map<String, String> header = awsv4.getHeaders();
        for (Map.Entry<String, String> entrySet : header.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            System.out.println("Header = "+key + "Value = "+value);
            restAssuredRequest.header(key, value);
        }

        restAssuredRequest.header("x-amz-content-sha256"
                ,"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");

        Response resp = restAssuredRequest.get("https://sqs.ap-south-1.amazonaws.com");

        System.out.println("resp code : "+ resp.getStatusCode());
        System.out.println("resp body : "+ resp.asString());
    }

    public static Properties getAWSProperties() throws IOException {
        FileInputStream fis = new
                FileInputStream(System.getProperty("user.dir")+"\\src\\test\\java\\Utils\\config.properties");
        awsProp.load(fis);
        return awsProp;
    }
}
