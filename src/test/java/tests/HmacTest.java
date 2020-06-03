package tests;

import Utils.HmacSha256Calculation;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.Properties;

public class HmacTest {
    private static Properties awsProp = new Properties();
    public static void main(String args[]) throws Exception {
        getAWSProperties();
        String secretAccessKey = awsProp.getProperty("awsSecretAccessKey");
        String dateStamp = awsProp.getProperty("dateStamp");
        String region = awsProp.getProperty("awsRegion");
        String serviceName = awsProp.getProperty("awsServiceName");
        System.out.println("secretAccessKey="+secretAccessKey);
        System.out.println("dateStamp="+dateStamp);
        System.out.println("region="+region);
        System.out.println("serviceName="+serviceName);
        System.out.println("signature in hex format : "+ toHexString(
                getSignatureKey(secretAccessKey,dateStamp,region,serviceName)
        ));
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    static byte[] HmacSHA256(String data, byte[] key) throws Exception {
        String algorithm="HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF-8"));
    }

    static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF-8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return kSigning;
    }

    public static Properties getAWSProperties() throws IOException {
        FileInputStream fis = new
                FileInputStream(System.getProperty("user.dir")+"\\src\\test\\java\\Utils\\config.properties");
        awsProp.load(fis);
        return awsProp;
    }
}
