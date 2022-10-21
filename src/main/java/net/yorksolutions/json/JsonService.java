package net.yorksolutions.json;

import antlr.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
public class JsonService {

    public JsonService() {
    }

    //1.IP Address
    public HashMap getIp(HttpServletRequest request){

        HashMap ipMap = new HashMap();
        ipMap.put("ip", request.getRemoteAddr());

        return  ipMap;
    }

    //2.HTTP Headers
    public HashMap getHeaders(@RequestHeader Map<String, String> headers){

        HashMap headersMap = new HashMap<>();

        headers.forEach((key, value) -> {
            headersMap.put(key, value);
        });

        return headersMap;
    }

//    public HashMap header(HttpServletRequest request){
//
//        HashMap map = new HashMap<>();
//
//        Enumeration<String> headerNameList = request.getHeaderNames();
//        while (headerNameList.hasMoreElements(){
//            String headerNameKey = headerNameList.nextElement();
//            String headerDataValue = request.getHeader(headerNameKey);
//            map.put(headerNameKey, headerDataValue);
//        }
//        return map;
//    }

    //3.Date & Time
    public HashMap dateTime(){
        Date currentDate = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String strDate = dateFormat.format(currentDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String strTime = timeFormat.format(currentDate);

        HashMap dateMap = new HashMap();
        dateMap.put("date", strDate);
        dateMap.put("time", strTime);
        dateMap.put("milliseconds_since_epoch", currentDate.getTime());
        return dateMap;
    }

    //4.Echo JSON
    public HashMap echo(HttpServletRequest request){

        String uri = request.getRequestURI();
        String[] uriArray = uri.split("/");

        HashMap map = new HashMap();
        for(int i=2; i<uriArray.length; i+=2){

            String key = uriArray[i];
            String value = "";
            if (i+1 < uriArray.length){
                value = uriArray[i +1];
            }

            map.put(key,value);
        }
        return map;
    }

    //TODO 5.Validate
    public HashMap validate (String jsonString){

        boolean isArray = jsonString.charAt(0) == '[';
        int length;

        HashMap map = new HashMap<>();

        try {

            int startTime = Instant.now().getNano();

            if (isArray) {
                JSONArray jsonArray = new JSONArray(jsonString);
                length = jsonArray.length();
            } else {
                JSONObject jsonObject = new JSONObject(jsonString);
                length = jsonObject.length();
            }

            int endTime = Instant.now().getNano();

            map.put("validate", true);
            map.put("object_or_array", isArray ? "array" : "object");
            map.put("parse_time_nanoseconds", endTime - startTime);
            map.put("size", length);
            map.put("empty", length > 0 ? false : true);

        } catch (JSONException exception) {

            map.put("validate", false);
            map.put("error", exception.getMessage());
            map.put("object_or_array", isArray ? "array" : "object");
            map.put("error info", "This error came from " + exception.getClass());

        }

        return map;
        }

    //6.Arbitrary JS Code
    public String code(HttpServletRequest request){
        return "alert(\"Your IP address is: " + request.getRemoteAddr() + "\");";
    }

    //7.Cookie https://dzone.com/articles/how-to-use-cookies-in-spring-boot
    public HashMap cookie(HttpServletResponse response){

        Date time = new Date();
        String epochTime = String.valueOf(time.getTime());

        Cookie cookie = new Cookie("jsontestdotcom", "ms:" + epochTime );
        response.addCookie(cookie);

        HashMap map = new HashMap();
        map.put("cookie_status", "Cookie set with name jsontestdotcom");

        return map;
    }

    //8.MD5: https://www.geeksforgeeks.org/md5-hash-in-java/
    public HashMap md5(HttpServletRequest request){

        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String subPath = StringUtils.stripFront(fullPath, "/md5");

        System.out.println(fullPath);
        System.out.println(subPath);

        HashMap mdMap = new HashMap();

        try {
            //getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //digest calculates MessageDigest of input digest() return array of byte
            byte[] messageDigest = md.digest(subPath.getBytes());
            //Convert byte array  into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            //Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32){
                hashtext = "0" + hashtext;
            }
            mdMap.put("Original", subPath);
            mdMap.put("md5", hashtext);
        }
        //if wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return mdMap;
    }
    public HashMap getMd5(String text){

        HashMap map = new HashMap();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            byte[] digest = md.digest();
            String hashValue = DatatypeConverter.printHexBinary(digest).toLowerCase();

            map.put("original", text);
            map.put("md5", hashValue);

        } catch (NoSuchAlgorithmException exception){
            System.out.println("Bad request");
            return new HashMap<>();
        }

        return map;
    }


}
