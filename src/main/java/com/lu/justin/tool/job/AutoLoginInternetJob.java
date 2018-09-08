package com.lu.justin.tool.job;

import com.lu.justin.tool.service.remote.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Profile(value = {"ludev"})
@Component
public class AutoLoginInternetJob {

    private final static Logger log = LoggerFactory.getLogger(AutoLoginInternetJob.class);

    private final static Map<LocalDate, Boolean> loginCache = new HashMap<>();

    @Resource
    private RemoteService remoteService;

    @Scheduled(cron = "*/10 * 1-5 * * ?")
    public void autoLogin163() {
        log.info("auto login internet by request 163.com");

        if (loginCache.getOrDefault(LocalDate.now(), Boolean.FALSE)) {
            log.info("auto login internet has already done~~~");
            return;
        }

        String resp = remoteService.get("https://www.163.com");
        if (resp.contains("网易首页")) {
            loginCache.clear();
            loginCache.put(LocalDate.now(), Boolean.TRUE);
            log.info("auto login internet has done~~~");
        }

        log.info("auto login internet job finish.");
    }
}
