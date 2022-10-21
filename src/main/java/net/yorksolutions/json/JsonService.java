package net.yorksolutions.json;

import antlr.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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

    //5.Validate

    //6.Arbitrary JS Code
    public String code(HttpServletRequest request){
        return "alert(\"Your IP address is: " + request.getRemoteAddr() + "\");";
    }

    //7.Cookie
    public HashMap cookie(HttpServletRequest response){
        Date time = new Date();
        String epochTime = String.valueOf(time.getTime());

        Cookie cookie = new Cookie("jsontestdotcom", "ms" + epochTime );
        response.addCookie(cookie);

        HashMap map = new HashMap();
        map.put("cookie_status", "Cookie set with name jsontestdotcom");

        return map;
    }

    //8.MD5: https://www.geeksforgeeks.org/md5-hash-in-java/
    public HashMap getMd(HttpServletRequest request){

        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String subPath = StringUtils.stripFront(fullPath, "md5/");

        System.out.println(fullPath);
        System.out.println(subPath);

        HashMap mdMap = new HashMap();

        try {
            //getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //digest90 calculates MessageDigest of input digest() return array of byte
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


}
