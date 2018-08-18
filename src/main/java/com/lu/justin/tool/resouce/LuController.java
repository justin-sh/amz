package com.lu.justin.tool.resouce;

import com.lu.justin.tool.util.Caches;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping(path = "/lu")
public class LuController {

    private final static Logger log = LoggerFactory.getLogger(LuController.class);

    @GetMapping(value = "/product-count")
    public Map<String, Map<String, Integer>> getT7ProductCount() {
        String d7 = LocalDateTime.now().minus(Duration.ofDays(7)).format(DateTimeFormatter.ISO_DATE);
        String d1 = LocalDateTime.now().minus(Duration.ofDays(1)).format(DateTimeFormatter.ISO_DATE);
        String d0 = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);

        Map<String, Integer> dd7 = new TreeMap<>(Caches.cache.getOrDefault(d7, Collections.emptyMap()));
        Map<String, Integer> dd1 = new TreeMap<>(Caches.cache.getOrDefault(d1, Collections.emptyMap()));
        Map<String, Integer> dd0 = new TreeMap<>(Caches.cache.getOrDefault(d0, Collections.emptyMap()));

        Map<String, Map<String, Integer>> respJson = new HashMap<>(3);
        respJson.put("d7", dd7);
        respJson.put("d1", dd1);
        respJson.put("d0", dd0);

        mergeData(dd0);
        mergeData(dd1);
        mergeData(dd7);

        return respJson;
    }

    private void mergeData(Map<String, Integer> data) {

        Map<String, Integer> merged = new HashMap<>();
        Map.Entry<String, Integer> prev = null;
        for (Map.Entry<String, Integer> e : data.entrySet()) {
            if (prev == null) {
                prev = e;
                continue;
            }
            LocalTime pt = LocalTime.parse(prev.getKey());
            LocalTime ct = LocalTime.parse(e.getKey());
            long diff = Duration.between(pt, ct).get(ChronoUnit.SECONDS);
            if (diff == 60) {
                continue;
            }
            int intMius = (int) diff / 60;
            double intv = (e.getValue() - prev.getValue()) / 1.0 / intMius;

            for (int i = 1; i < intMius; i++) {
                LocalTime tmp = pt.plus(Duration.ofMinutes(i));
                merged.put(tmp.format(DateTimeFormatter.ISO_TIME).substring(0, 5), prev.getValue() + (int) (i * intv));
            }
            prev = e;
        }

        data.putAll(merged);
    }
}
