package com.lu.justin.tool.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lu.justin.tool.service.remote.RemoteService;
import com.lu.justin.tool.util.Caches;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;

//@Profile(value = {"ludev", "default"})
//@Component
public class ProductCountJob {

    private final static Logger log = LoggerFactory.getLogger(ProductCountJob.class);

    @Resource
    private RemoteService remoteService;

    @Scheduled(cron = "1/20 * * * * ?")
    public void getProductCount() throws IOException {
        String url = "https://list.lu.com/list/service/productListing/all-counts";
        String r = remoteService.get(url);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(r, Map.class);
        int cnt = (int) json.getOrDefault("p2pTransferCount", 0);

        LocalDateTime dt = LocalDateTime.now();
        String today = dt.format(DateTimeFormatter.ISO_DATE);
        String hm = dt.format(DateTimeFormatter.ISO_TIME).substring(0, 5);
        if (!Caches.cache.containsKey(today)) {
            Caches.cache.put(today, new TreeMap<>());
        }
        Caches.cache.get(today).put(hm, cnt);
        log.info(today + " " + hm + "=" + cnt);
    }

    @Scheduled(cron = "10 1 * * * ?")
    public void saveData2File() {
        saveCache();
    }

    @PostConstruct
    public void init() {
        try {

            Path p = Paths.get("/tmp/data.txt");
            if (!Files.exists(p)) {
                Files.write(p, "{}".getBytes(), StandardOpenOption.CREATE);
                return;
            }

            String data = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, Integer>> json = mapper.readValue(data, Map.class);

            Caches.cache.putAll(json);

            log.info("load data to cache from file success~~");
        } catch (IOException e) {
            log.error("read data to file failed!", e);
        }
    }

    @PreDestroy
    public void destroy() {
        saveCache();
    }

    private void saveCache() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String d7 = LocalDateTime.now().minus(7 * 24, ChronoUnit.HOURS).format(DateTimeFormatter.ISO_DATE);
            Caches.cache.forEach((k, v) -> {
                if (k.compareTo(d7) <= -1) {
                    Caches.cache.remove(k);
                }
            });
            mapper.writeValue(Files.newOutputStream(Paths.get("/tmp/data.txt"), StandardOpenOption.WRITE), Caches.cache);
            log.info("save cache to file success~");
        } catch (IOException e) {
            log.error("save data to file failed!", e);
        }

    }
}
