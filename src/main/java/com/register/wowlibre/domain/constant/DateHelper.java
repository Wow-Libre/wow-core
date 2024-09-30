package com.register.wowlibre.domain.constant;

import lombok.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateHelper {

  private static final Map<String, ZoneId> ZONE_IDS = createZoneIdsMap();

  /**
   * Get Zone Id By Site
   */
  public static ZoneId getZoneIdBySite(String siteId) {
    return ZONE_IDS.get(siteId);
  }

  /**
   * Get LocalDateTime
   */
  public static LocalDateTime getLocalDateTime(Date date) {
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  /**
   * Get LocalDateTimeAndSiteId
   */
  public static LocalDateTime getLocalDateTime(Date date, String siteId) {
    return LocalDateTime.ofInstant(date.toInstant(), getZoneIdBySite(siteId));
  }

  /**
   * Format Date From String
   */
  public static Date formatDateFromString(String dateToParse, ZoneId zoneId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime localDateTime = LocalDateTime.parse(dateToParse, formatter);
    return Date.from(localDateTime.atZone(zoneId).toInstant());
  }

  public static Date localDateFromDate(@NonNull LocalDate localDate, @NonNull ZoneId zoneId) {
    return Date.from(localDate.atStartOfDay(zoneId).toInstant());
  }

  public static String dateToString(@NonNull Date date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    return dateFormat.format(date);
  }

  public static Date incrementMinutes(Date baseDate, Integer minute) {
    return Optional.ofNullable(baseDate).map(date -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.add(Calendar.MINUTE, minute);
      return cal.getTime();
    }).orElse(null);
  }

    private static Map<String, ZoneId> createZoneIdsMap() {
        Map<String, ZoneId> zoneIdMap = new HashMap<>();
        zoneIdMap.put("WLUS", ZoneId.of("America/New_York"));
        zoneIdMap.put("WLMX", ZoneId.of("America/Mexico_City"));
        zoneIdMap.put("WLBS", ZoneId.of("America/Nassau"));
        zoneIdMap.put("WLCU", ZoneId.of("America/Havana"));
        zoneIdMap.put("WLJM", ZoneId.of("America/Jamaica"));
        zoneIdMap.put("WLHT", ZoneId.of("America/Port-au-Prince"));
        zoneIdMap.put("WLDO", ZoneId.of("America/Santo_Domingo"));
        zoneIdMap.put("WLGT", ZoneId.of("America/Guatemala"));
        zoneIdMap.put("WLHN", ZoneId.of("America/Tegucigalpa"));
        zoneIdMap.put("WLNI", ZoneId.of("America/Managua"));
        zoneIdMap.put("WLCR", ZoneId.of("America/Costa_Rica"));
        zoneIdMap.put("WLPA", ZoneId.of("America/Panama"));
        zoneIdMap.put("WLBZ", ZoneId.of("America/Belize"));
        zoneIdMap.put("WLSV", ZoneId.of("America/El_Salvador"));
        zoneIdMap.put("WLCO", ZoneId.of("America/Bogota"));
        zoneIdMap.put("WLVE", ZoneId.of("America/Caracas"));
        zoneIdMap.put("WLGY", ZoneId.of("America/Guyana"));
        zoneIdMap.put("WLSR", ZoneId.of("America/Paramaribo"));
        zoneIdMap.put("WLGF", ZoneId.of("America/Cayenne"));
        zoneIdMap.put("WLBR", ZoneId.of("America/Sao_Paulo"));
        zoneIdMap.put("WLPE", ZoneId.of("America/Lima"));
        zoneIdMap.put("WLBO", ZoneId.of("America/La_Paz"));
        zoneIdMap.put("WLPY", ZoneId.of("America/Asuncion"));
        zoneIdMap.put("WLUY", ZoneId.of("America/Montevideo"));
        zoneIdMap.put("WLAR", ZoneId.of("America/Argentina/Buenos_Aires"));
        zoneIdMap.put("WLCL", ZoneId.of("America/Santiago"));
        zoneIdMap.put("WLEC", ZoneId.of("America/Guayaquil"));
        return Collections.unmodifiableMap(zoneIdMap);
    }

}
