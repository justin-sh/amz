package com.lu.justin.tool.resouce;

import com.lu.justin.tool.dao.ProductSummaryDAO;
import com.lu.justin.tool.dao.ProductTransferRateDAO;
import com.lu.justin.tool.dao.dto.ProductSummaryDTO;
import com.lu.justin.tool.dao.dto.ProductTransferRateDTO;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping(path = "/lu")
public class LuController {

    private final static Logger log = LoggerFactory.getLogger(LuController.class);

    @Resource
    private ProductSummaryDAO productSummaryDAO;

    @Resource
    private ProductTransferRateDAO productTransferRateDAO;

    @GetMapping(value = "/product-count")
    public Map<String, Object> getT7ProductCount(@RequestParam(value = "all", required = false, defaultValue = "false") String isAll) {

        log.info("/product-count get data for param:isAll={}", isAll);

        LocalDateTime dateTime0 = LocalDateTime.now();
        LocalDateTime dateTime1 = dateTime0.minusDays(1);
        LocalDateTime dateTime7 = dateTime0.minusDays(7);

        Map<String, Integer> dd0 = new TreeMap<>();
        Map<String, Integer> dd1 = new TreeMap<>();
        Map<String, Integer> dd7 = new TreeMap<>();

        List<ProductSummaryDTO> psListD0 = productSummaryDAO.findByDateGreaterThanEqualAndCountGreaterThanOrderByDateDesc(dateTime0.toLocalDate(), 0);

        if (Boolean.parseBoolean(isAll)) {
            List<ProductSummaryDTO> psListD1 = productSummaryDAO.findByDateGreaterThanEqualAndCountGreaterThanOrderByDateDesc(dateTime1.toLocalDate(), 0);
            List<ProductSummaryDTO> psListD7 = productSummaryDAO.findByDateGreaterThanEqualAndCountGreaterThanOrderByDateDesc(dateTime7.toLocalDate(), 0);
            psListD1.forEach(e -> dd1.put(DateFormatUtils.format(e.getDate(), "HH:mm"), e.getCount()));
            psListD7.forEach(e -> dd7.put(DateFormatUtils.format(e.getDate(), "HH:mm"), e.getCount()));
            dd0.putIfAbsent("00:00", 0);
            dd7.putIfAbsent("00:00", 0);

        }

        psListD0.forEach(e -> dd0.put(DateFormatUtils.format(e.getDate(), "HH:mm"), e.getCount()));
        dd0.putIfAbsent("00:00", 0);

        LocalDate today = LocalDate.now();
        ProductTransferRateDTO transferRateDTO = productTransferRateDAO.findByDate(today);
        Map<String, Object> successRate = new HashMap<>(2);
        if (Objects.isNull(transferRateDTO)) {
            successRate.put("actionType", "P2P_TRANSFER");
            successRate.put("avgSuccessRatio", -1);
        } else {
            successRate.put("actionType", transferRateDTO.getActionType());
            successRate.put("avgSuccessRatio", transferRateDTO.getAvgSuccessRatio());
        }

        Map<String, Object> respJson = new HashMap<>(3);

        mergeData(dd0);
        mergeData(dd1);
        mergeData(dd7);

        respJson.put("d7", dd7);
        respJson.put("d1", dd1);
        respJson.put("d0", dd0);

        respJson.put("successRate", successRate);
        respJson.put("totalCount", 0);

        if (!psListD0.isEmpty()) {
            // data may not be real...
            respJson.put("totalCount", psListD0.get(0).getCount());
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
