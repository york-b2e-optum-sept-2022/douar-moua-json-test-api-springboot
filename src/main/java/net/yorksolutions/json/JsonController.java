package net.yorksolutions.json;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class JsonController {

    private JsonService jsonService;

    public JsonController(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    //1.IP Address
    @GetMapping("/ip")
    public HashMap getIp(HttpServletRequest request) {
        return this.jsonService.getIp(request);
    }

    //2.HTTP Headers
    @GetMapping("/headers")
    public HashMap headers(@RequestHeader Map<String, String> headers) {
        return this.jsonService.getHeaders(headers);
    }

    //3.Date & Time
    @GetMapping({"/date", "/time"})
    public HashMap currentDate() {
        return this.jsonService.dateTime();
    }

    //4.Echo JSON
    @GetMapping("/echo/**")
    public HashMap echo(HttpServletRequest request){
        return this.jsonService.echo(request);
    }

    //5.Validate

    //6.Arbitrary JS Code
    @GetMapping("/code")
    public String code(HttpServletRequest request){
        return this.jsonService.code(request);
    }

    //7.Cookie
    @GetMapping("/cookie")
    public HashMap cookie(HttpServletResponse response){
        return this.jsonService.cookie(response);
    }

    //8.MD5
    @GetMapping("/md5/**")
    public HashMap getMd(HttpServletRequest request) {
        return this.jsonService.getMd(request);
    }

}
