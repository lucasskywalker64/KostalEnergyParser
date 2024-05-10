package com.github.lucasskywalker64;

import clients.DsmFileStationClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.tinylog.Logger;
import requests.DsmAuth;

/**
 * The Main class schedules tasks to fetch data periodically, process it, write it to an Excel
 * file, and upload the file to a remote server.
 */
public class Main {

  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private static final java.util.Properties properties = new Properties();

  /**
   * The main method loads environment properties, initializes authentication, and sets up
   * scheduled tasks.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    try (FileInputStream stream = new FileInputStream(new File(
        Main.class.getProtectionDomain().getCodeSource().getLocation().getPath())
        .getParentFile().getAbsolutePath() + "/env.properties")) {
      properties.load(stream);
      DsmAuth auth = DsmAuth.fromProperties(properties);
      KostalData kostalData = new KostalData(properties);

      LocalDateTime localNow = LocalDateTime.now();
      ZoneId currentZone = ZoneId.systemDefault();
      ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
      ZonedDateTime zonedNextMinute = zonedNow.withSecond(1).withNano(0).plusMinutes(1);
      ZonedDateTime zonedNextDay = zonedNow.withHour(0).withMinute(0).withSecond(0).plusDays(1);


      AtomicInteger schedulerCount = new AtomicInteger();
      AtomicLong countToNextDay = new AtomicLong(Duration.between(zonedNextMinute,
          zonedNextDay).getSeconds() / 60);
      Sheet sheet = new Sheet();
      scheduler.scheduleAtFixedRate(() -> {
        if (schedulerCount.get() == 0) {
          sheet.createSheet();
        }
        if (schedulerCount.get() == countToNextDay.get()) {
          schedulerCount.set(0);
          countToNextDay.set(1440);
          try {
            sheet.writeToFile(kostalData);
            DsmFileStationClient fileStationClient = DsmFileStationClient.login(auth);
            boolean uploadSuccess = false;
            int maxRetries = 3;
            while (!uploadSuccess && maxRetries > 0) {
              uploadSuccess = fileStationClient.upload(properties.getProperty("uploadPath"),
                  sheet.getFileLocation()).call().isSuccess();
              maxRetries--;
            }
            fileStationClient.logout();
            Files.delete(Path.of(sheet.getFileLocation()));
            Logger.info("Upload successful");
          } catch (IOException e) {
            Logger.error(e);
          }
        }
        kostalData.fetchNewData();
        Logger.info("Fetched new data");
        schedulerCount.incrementAndGet();
      }, Duration.between(zonedNow, zonedNextMinute).getSeconds(), 60, TimeUnit.SECONDS);
    } catch (IOException e) {
      Logger.error(e.getMessage(), e);
    }
  }
}