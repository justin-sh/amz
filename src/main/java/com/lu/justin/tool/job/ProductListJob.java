package com.lu.justin.tool.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lu.justin.tool.dao.ProductInfoDAO;
import com.lu.justin.tool.dao.ProductSummaryDAO;
import com.lu.justin.tool.dao.ProductTransferRateDAO;
import com.lu.justin.tool.dao.dto.BaseDTO;
import com.lu.justin.tool.dao.dto.ProductDTO;
import com.lu.justin.tool.dao.dto.ProductSummaryDTO;
import com.lu.justin.tool.dao.dto.ProductTransferRateDTO;
import com.lu.justin.tool.service.remote.RemoteService;
import com.lu.justin.tool.util.Constant;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Profile(value = {"ludev", "default"})
@Component
public class ProductListJob {

    private final static int MAX_EXECUTE_TIMES = 1000;

    private final static Logger log = LoggerFactory.getLogger(ProductListJob.class);

    @Resource
    private RemoteService remoteService;

    @Resource
    private ProductInfoDAO productInfoDAO;

    @Resource
    private ProductSummaryDAO productSummaryDAO;

    @Resource
    private ProductTransferRateDAO productTransferRateDAO;

    @Scheduled(cron = "1/5 * * * * ?")
    public void getProductInfo() {

        int pageCount = 1;
        int curPage = 0;
        String url = "https://list.lu.com/list/transfer-p2p?minMoney=&maxMoney=&minDays=&maxDays=&minRate=&maxRate=&mode=&tradingMode=&isOverdueTransfer=&isCx=&currentPage=%s&orderCondition=&isShared=&canRealized=&productCategoryEnum=&notHasBuyFeeRate=&riskLevel=";

        while (curPage++ < pageCount && curPage < MAX_EXECUTE_TIMES) {
            try {
                log.info("process page {} to get product info", curPage);
                String r = remoteService.get(String.format(url, curPage));

                Document root = Jsoup.parse(r);
                pageCount = Integer.parseInt(root.select("#pageCount").attr("value"));
                log.info("total page count is {}", pageCount);
                Elements prdList = root.select("li.product-list");

                log.info("current page {} get product list count {}", curPage, prdList.size());
                List<ProductDTO> prdInfoList = new ArrayList<>();
                for (Element e : prdList) {
                    prdInfoList.add(generatePrdInfo(e));
                }

                List<String> prdIds = prdInfoList.stream().map(ProductDTO::getPrdId).collect(Collectors.toList());
                List<ProductDTO> existPrds = productInfoDAO.selectBySearcher(prdIds);
                Map<String, ProductDTO> existMapPrds = existPrds.stream().collect(Collectors.toMap(ProductDTO::getPrdId, x -> x));

                log.info("{} product exists and will update id & validFrom time to origin", existPrds.size());
                //update product info if exists
                prdInfoList.forEach(x -> {
                    if (existMapPrds.containsKey(x.getPrdId())) {
                        x.setId(existMapPrds.get(x.getPrdId()).getId());
                        x.setValidFrom(existMapPrds.get(x.getPrdId()).getValidFrom());
                        x.setCreatedAt(existMapPrds.get(x.getPrdId()).getCreatedAt());
                        x.setCreatedBy(existMapPrds.get(x.getPrdId()).getCreatedBy());
                    }
                });

                productInfoDAO.saveAll(prdInfoList);
                log.info("product info(count:{}) save to db ok.", prdInfoList.size());

            } catch (Exception e) {
                log.warn("some error occur when get product info from internet~~", e);
            }
        }
    }

    private ProductDTO generatePrdInfo(Element e) {

        ProductDTO.Builder b = new ProductDTO.Builder();

        Element href = e.selectFirst(".product-name a");
        b.name(href.text());
        b.isTransferred(e.select(".product-name").text().contains("转"));
        try {
            URIBuilder u = new URIBuilder(href.attr("href"));
            u.getQueryParams().forEach(x -> {
                if ("productId".equals(x.getName())) {
                    b.prdId(x.getValue());
                } else if ("productCategory".equals(x.getName())) {
                    b.category(x.getValue());
                }
            });
        } catch (URISyntaxException e1) {
            log.warn("cannot parse productId & category from url {}", href.attr("href"));
        }
        b.interest(e.select(".interest-rate p").text().replace("%", ""));
        b.investPeriod(e.select(".invest-period p").text().replace("个月", ""));
        b.value(e.select(".product-value p").text().replace("元", "").replaceAll(",", ""));
        b.nextDate(e.selectFirst(".acceptance-bank span").text().replace("预计下一收款日：", ""));
        Elements fee = e.select(".acceptance-bank span.ml20");
        if (!fee.isEmpty()) {
            b.fee(fee.text().replace("信息服务费率：", "").replace("%/月", ""));
        }
        b.amount(e.select(".product-amount em").text().replaceAll(",", ""));
        Date now = new Date();
        b.validFrom(now);
        b.validTo(now);

        return b.build();
    }

    @Scheduled(cron = "*/2 * * * * ?")
    public void summariseProductPerMinute() {
        LocalDateTime mm1 = LocalDateTime.now();
        summariseProductByTime(mm1);

        if (mm1.getSecond() < 10) {
            mm1 = mm1.minusMinutes(1);
            summariseProductByTime(mm1);
        }

    }


    @Scheduled(cron = "1/10 * 1-5 * * ?")
    public void getTransferRate() {
        log.info("get secondary market stat");

        LocalDate today = LocalDate.now();
        ProductTransferRateDTO transferRateDTO = productTransferRateDAO.findByDate(today);
        if (!Objects.isNull(transferRateDTO)) {
            log.info("secondary market stat has got and return");
            return;
        }

        String resp = remoteService.get("https://list.lu.com/list/api/product/secondary-market-stat?actionType=P2P_TRANSFER");
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> json = mapper.readValue(resp, Map.class);
            ArrayList data = (ArrayList) json.get("data");
            System.out.println(data);
            HashMap r1 = (HashMap) data.get(0);

            String actionType = (String) r1.getOrDefault("actionType", "P2P_TRANSFER");
            BigDecimal avgSuccessRatio = BigDecimal.valueOf((Double) r1.getOrDefault("avgSuccessRatio", 0));

            ProductTransferRateDTO ptr = new ProductTransferRateDTO(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()), actionType, avgSuccessRatio);
            productTransferRateDAO.save(ptr);
            log.info("secondary market stat save to db..");
        } catch (IOException e) {
            log.warn("get secondary market info failed! error:{}", e, e);
        }

        log.info("get secondary market stat finish..");
    }

    private void summariseProductByTime(LocalDateTime mm) {

//        LocalDateTime mm1 = LocalDateTime.now().minusMinutes(1);
        // BigDecimal cannot sum in mongodb
//        log.info("result:{}", productInfoDAO.groupInfoByDate(mm1));

        LocalDateTime from = mm.withSecond(0).withNano(0);
        LocalDateTime to = from.withSecond(59);

        List<ProductDTO> list = productInfoDAO.findByCondition(Query.query(Criteria.where("validFrom").lte(to).and("validTo").gte(from)));

        log.info("get product info count:{}", list.size());
        int count = 0;
        BigDecimal sumValue = BigDecimal.ZERO;
        BigDecimal sumAmount = BigDecimal.ZERO;
        int count1 = 0;
        int count3 = 0;
        int count5 = 0;
        int count10 = 0;
        int count99 = 0;
        BigDecimal maxMarkDown = BigDecimal.ZERO;
        BigDecimal maxMarkDownRate = BigDecimal.ZERO;
        for (ProductDTO p : list) {
            count++;
            sumValue = sumValue.add(p.getValue());
            sumAmount = sumAmount.add(p.getAmount());
            if (p.getAmount().compareTo(Constant.W1) < 0) {
                count1++;
            } else if (p.getAmount().compareTo(Constant.W3) < 0) {
                count3++;
            } else if (p.getAmount().compareTo(Constant.W5) < 0) {
                count5++;
            } else if (p.getAmount().compareTo(Constant.W10) < 0) {
                count10++;
            } else {
                count99++;
            }

            if (p.getValue().subtract(p.getAmount()).subtract(maxMarkDown).compareTo(BigDecimal.ZERO) > 0) {
                maxMarkDown = p.getValue().subtract(p.getAmount());
                maxMarkDownRate = maxMarkDown.divide(p.getValue(), new MathContext(4, RoundingMode.HALF_EVEN));
            }
        }

        Date spKey = Date.from(from.atZone(ZoneId.systemDefault()).toInstant());
        ProductSummaryDTO ps = productSummaryDAO.findByDate(spKey);

        if (ps == null) {
            ps = new ProductSummaryDTO(spKey);
            ps.setBaseInfo();
        }

        ps.setAmount(sumAmount);
        ps.setCount(count);
        ps.setValue(sumValue);
        ps.setCount1(count1);
        ps.setCount3(count3);
        ps.setCount5(count5);
        ps.setCount10(count10);
        ps.setCount99(count99);
        ps.setMaxMarkDown(maxMarkDown);
        ps.setMaxMarkDownRate(maxMarkDownRate);
        ps.setUpdatedAt(new Date());
        ps.setUpdatedBy(BaseDTO.SYS);

        productSummaryDAO.save(ps);

        log.info("sum result:count {}, value:{} amount:{}", count, sumValue, sumAmount);
    }

}
