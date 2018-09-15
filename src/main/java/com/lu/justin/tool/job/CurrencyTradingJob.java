package com.lu.justin.tool.job;


import com.lu.justin.tool.dao.CurrencyRateDAO;
import com.lu.justin.tool.dao.dto.CurrencyRateDTO;
import com.lu.justin.tool.service.remote.RemoteService;
import com.lu.justin.tool.util.Money;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

@Component
public class CurrencyTradingJob {

    private final static Logger log = LoggerFactory.getLogger(CurrencyTradingJob.class);


    @Resource
    private RemoteService remoteService;

    @Resource
    private CurrencyRateDAO currencyRateDAO;

    @Scheduled(cron = "1/3 * * * * ?")
    public void getRate() throws ParseException {
        log.info("get rate...");
        String resp = remoteService.get("http://www.currencydo.com/index/api/hljs/hbd/AUD_CNY/?t=" + RandomUtils.nextDouble(0, 1));

        String[] resps = resp.split("#");
        log.info("get response from currentydo:{}", Arrays.toString(resps));

        Date d = DateUtils.parseDate(resps[5], "yyyy-MM-dd HH:mm:ss");

        CurrencyRateDTO c = currencyRateDAO.findByDateAndFromAndTo(d, "AUD", "CNY");

        if (c == null) {
            c = new CurrencyRateDTO(d, "AUD", "CNY");
        }

        c.setRate(Money.fromCNY(new BigDecimal(resps[4])));

        currencyRateDAO.save(c);

        log.info("save currency rate to db~~");

    }
}
