package com.lu.justin.tool.job;

import com.lu.justin.tool.dao.ProductInfoDAO;
import com.lu.justin.tool.dao.dto.ProductDTO;
import com.lu.justin.tool.service.remote.RemoteService;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProductListJob {

    private final static int MAX_EXECUTE_TIMES = 1000;

    private final static Logger log = LoggerFactory.getLogger(ProductListJob.class);

    @Resource
    private RemoteService remoteService;

    @Resource
    private ProductInfoDAO productInfoDAO;

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
        b.fee(e.select(".acceptance-bank span.ml20").text().replace("信息服务费率：", "").replace("%/月", ""));
        b.amount(e.select(".product-amount em").text().replaceAll(",", ""));
        Date now = new Date();
        b.validFrom(now);
        b.validTo(now);

        return b.build();
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void summariseProductPerMinute() {
        //with no second & nono
//        Date now = Date.from(LocalDateTime.now().withSecond(0).withNano(0).atZone(ZoneId.systemDefault()).toInstant());
//        Example<ProductDTO> q = Example.of(new ProductDTO.Builder().validFrom(now).validTo(now).build());

//        ExampleMatcher m = ExampleMatcher.matching().withMatcher("validFrom", ExampleMatcher.GenericPropertyMatchers..of())

        log.info("result:{}", productInfoDAO.groupInfoByDate(LocalDateTime.now().minusMinutes(1)));

        LocalDateTime from = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime to = from.withSecond(59);

        List<ProductDTO> list = productInfoDAO.findByCondition(Query.query(Criteria.where("validFrom").lte(to).and("validTo").gte(from)));

        log.info("========\n{}\n", list);
        int count = 0;
        BigDecimal sumValue = BigDecimal.ZERO;
        BigDecimal subAmount = BigDecimal.ZERO;
        for (ProductDTO p : list) {
            count++;
            sumValue = sumValue.add(p.getValue());
            subAmount = subAmount.add(p.getAmount());
        }

        log.info("sum result:{} {} {}", count, sumValue, subAmount);
    }

}
