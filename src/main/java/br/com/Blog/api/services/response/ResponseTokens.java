package br.com.Blog.api.services.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ResponseTokens {

    private String token;
    private String refresh;

    @Value("${app.jwt.expiration}")
    private long EXPIRETION_ACCESS_TOKEN;

    @Value("${app.jwt.expiration.refresh_token}")
    private long EXPIRATION_REFRESH_TOKEN;

    public ResponseTokens(String token, String refresh) {
        this.token = token;
        this.refresh = refresh;
    }

    public Map<String, String> showTokens() {
        Map<String, String> res = new HashMap<>();

        res.put("token", this.token);
        res.put("refresh", this.refresh);
        res.put("expiretion_refresh_token", String.valueOf(new Date(System.currentTimeMillis() + this.EXPIRATION_REFRESH_TOKEN )));
        res.put("expiretion_token", String.valueOf(new Date(System.currentTimeMillis() + this.EXPIRETION_ACCESS_TOKEN )));

        return res;
    }
}
