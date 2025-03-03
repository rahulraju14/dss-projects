package com.dss.auditlog;

import lombok.extern.slf4j.Slf4j;
import org.antlr.stringtemplate.language.ArrayIterator;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Slf4j
public class Test {
    private static DateTimeFormatter ISO_86_01_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static void main(String[] args) {
        String dateStr = "2024-08-21T08:54:27.688Z";
        String dateStr1 = "2024-08-21T08:54Z";
        Pattern pattern = Pattern.compile("(?<=\\.)\\d+(?=Z)");
        Matcher matcher = pattern.matcher(dateStr);
        matcher.find();
        String milliSeconds = matcher.group();
//        if(milliSeconds.length() < 3){
            milliSeconds = StringUtils.rightPad(milliSeconds, 3, "0");
            dateStr = matcher.replaceFirst(milliSeconds);
//        }

        OffsetDateTime date = OffsetDateTime.parse(dateStr, ISO_86_01_DATE_TIME_FORMATTER);
        log.info("Parsed date string: {} to Local DateTime:{}", dateStr, date);
        System.out.println("I am here...");
    }
}
