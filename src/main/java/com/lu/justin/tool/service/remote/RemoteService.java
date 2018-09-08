package com.lu.justin.tool.service.remote;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class RemoteService {

    private static final Logger log = LoggerFactory.getLogger(RemoteService.class);

    @Resource
    private HttpClient httpClient;

    public String get(String url) {
        HttpGet opt = new HttpGet(url);
        long _s = System.currentTimeMillis();
        try {
            HttpResponse response = httpClient.execute(opt);
            if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
                throw new RuntimeException("Http request failed!" + response.getStatusLine());
            }
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException("Http request failed!", e);
        } finally {
            log.info("http get execute finish![elapsed:{}ms][url:{}]", (System.currentTimeMillis() - _s), url);
        }
    }
}
