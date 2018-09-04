package com.lu.justin.tool.resouce;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lu.justin.tool.dao.ProductSummaryDAO;
import com.lu.justin.tool.dao.dto.ProductSummaryDTO;
import com.lu.justin.tool.service.remote.RemoteService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "/lu")
public class LuController {

    private final static Logger log = LoggerFactory.getLogger(LuController.class);

    private final static Map<String, Integer> cacheOfTotalCount = new HashMap<>(2);
    private final static Map<String, Object> cacheOfSuccessRate = new HashMap<>(2);

    @Resource
    private RemoteService remoteService;

    @Resource
    private ProductSummaryDAO productSummaryDAO;


    @GetMapping(value = "/product-count")
    public Map<String, Object> getT7ProductCount(@RequestParam(value = "all", required = false, defaultValue = "false") String isAll) {

        log.info("/product-count get data for param:isAll={}", isAll);

        LocalDateTime dateTime0 = LocalDateTime.now();
        LocalDateTime dateTime1 = dateTime0.minusDays(1);
        LocalDateTime dateTime7 = dateTime0.minusDays(7);
        String d0 = dateTime0.format(DateTimeFormatter.ISO_DATE);
        String d1 = dateTime1.format(DateTimeFormatter.ISO_DATE);
        String d7 = dateTime7.format(DateTimeFormatter.ISO_DATE);

        Map<String, Integer> dd0 = new TreeMap<>();
        Map<String, Integer> dd1 = new TreeMap<>();
        Map<String, Integer> dd7 = new TreeMap<>();

        List<ProductSummaryDTO> psListD0 = productSummaryDAO.findByDateGreaterThanEqualOrderByDateDesc(dateTime0.toLocalDate());

        if (Boolean.parseBoolean(isAll)) {
//            dd1.putAll(Caches.cache.getOrDefault(d1, Collections.emptyMap()));
//            dd7.putAll(Caches.cache.getOrDefault(d7, Collections.emptyMap()));
            List<ProductSummaryDTO> psListD1 = productSummaryDAO.findByDateGreaterThanEqualOrderByDateDesc(dateTime1.toLocalDate());
            List<ProductSummaryDTO> psListD7 = productSummaryDAO.findByDateGreaterThanEqualOrderByDateDesc(dateTime7.toLocalDate());
            psListD1.forEach(e -> dd1.put(DateFormatUtils.format(e.getDate(), "HH:mm"), e.getCount()));
            psListD7.forEach(e -> dd7.put(DateFormatUtils.format(e.getDate(), "HH:mm"), e.getCount()));
        }

        psListD0.forEach(e -> dd0.put(DateFormatUtils.format(e.getDate(), "HH:mm"), e.getCount()));


        String ymdhms = dateTime0.format(DateTimeFormatter.ISO_DATE_TIME);

        List<CompletableFuture> tasks = new ArrayList<>();
        if (!cacheOfTotalCount.containsKey(ymdhms)) {
            cacheOfTotalCount.clear();
            tasks.add(CompletableFuture.runAsync(() -> {
                LocalDateTime _s = LocalDateTime.now();
                try {
                    String url = "https://list.lu.com/list/service/productListing/all-counts";
                    String r = remoteService.get(url);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> json = mapper.readValue(r, Map.class);
                    cacheOfTotalCount.put(ymdhms, (int) json.getOrDefault("p2pTransferCount", 0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LocalDateTime _e = LocalDateTime.now();
                log.info("Get total count: {} [from:{} to:{}]", Duration.between(_s, _e), _s, _e);
            }));
        }

        if (!cacheOfSuccessRate.containsKey(d0)) {
            cacheOfSuccessRate.clear();
            tasks.add(CompletableFuture.runAsync(() -> {
                LocalDateTime _s = LocalDateTime.now();
                try {
                    String url = "https://list.lu.com/list/api/product/secondary-market-stat?actionType=P2P_TRANSFER";
                    String r = remoteService.get(url);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> json = mapper.readValue(r, Map.class);
                    cacheOfSuccessRate.put(d0, json.getOrDefault("data", Collections.EMPTY_MAP));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LocalDateTime _e = LocalDateTime.now();
                log.info("Get success rate:{} [from:{} to:{}]", Duration.between(_s, _e), _s, _e);
            }));
        }

        tasks.forEach((t) -> {
            try {
                t.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        Map<String, Object> respJson = new HashMap<>(3);

        mergeData(dd0);
        mergeData(dd1);
        mergeData(dd7);

        respJson.put("d7", dd7);
        respJson.put("d1", dd1);
        respJson.put("d0", dd0);

        respJson.put("totalCount", cacheOfTotalCount.getOrDefault(ymdhms, -1));
        respJson.put("successRate", cacheOfSuccessRate.getOrDefault(d0, "{}"));

        if (!psListD0.isEmpty()) {
            respJson.put("count1", psListD0.get(0).getCount1());
            respJson.put("count3", psListD0.get(0).getCount3());
            respJson.put("count5", psListD0.get(0).getCount5());
            respJson.put("count10", psListD0.get(0).getCount10());
            respJson.put("count99", psListD0.get(0).getCount99());
        }

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
