package com.zsc.hahamall.coupon;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

//@SpringBootTest
public class HahamallCouponApplicationTests {

	@Test
	public void contextLoads() {
		LocalDate now = LocalDate.now();
		LocalDate plus = now.plusDays(1);
		LocalDate plus2 = now.plusDays(2);
		LocalTime min = LocalTime.MIN;
		LocalTime max = LocalTime.MAX;
		System.out.println(plus);
		System.out.println(plus2);
		LocalDateTime start = LocalDateTime.of(now, min);
		LocalDateTime end = LocalDateTime.of(plus2, max);
		System.out.println(start);
		System.out.println(end);
		System.out.println("=======================================");
		System.out.println(startTime());
	}
	public String startTime(){
		LocalDate now = LocalDate.now();
		LocalTime min = LocalTime.MIN;
		LocalDateTime start = LocalDateTime.of(now, min);
		String sqlStartTime = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		return sqlStartTime;
	}


}
