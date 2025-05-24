package br.com.Blog.api.services.response;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseDefault {

    public Map<String, Object> response(String message, Integer code, String url, Object obj, boolean status) {
        Map<String, Object> body = new HashMap<>();

        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());
        body.put("statusCode", code);
        body.put("url", url);
        body.put("result", obj);
        body.put("success", status);

        return body;
    }

}
