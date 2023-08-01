package com.myblog.api.date;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
public class DateTimeTest {

    @Test
    public void test() {
        LocalDateTime dateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zoneDateTime = dateTime.atZone(zoneId);

        Date now = Date.from(dateTime.atZone(zoneId).toInstant());

        Date now2 = java.sql.Timestamp.valueOf(dateTime);

        Date now3 = new Date();

        log.info("datetime={}",dateTime);
        log.info("zoneDateTime={}", zoneDateTime);
        log.info("date ={}", now);
        log.info("date2 ={}", now2);
        log.info("date3 ={}", now3);
    }
}
