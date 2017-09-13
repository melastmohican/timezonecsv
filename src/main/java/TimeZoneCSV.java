import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.zone.ZoneOffsetTransitionRule;
import java.util.*;


public class TimeZoneCSV {
    public static void main(String[] args) {

        // Get the set of all time zone IDs.
        Set<String> allZones = ZoneId.getAvailableZoneIds();

        // Create a List using the set of zones and sort it.
        List<String> zoneList = new ArrayList<String>(allZones);
        //Collections.sort(zoneList);

        Collections.sort(zoneList, new Comparator<String>() {

            public int compare(final String t1, final String t2) {
                return ((Integer) TimeZone.getTimeZone(t1).getRawOffset()).compareTo(TimeZone.getTimeZone(t2).getRawOffset());
            }
        });

        ZonedDateTime zldt = LocalDateTime.now().atZone(ZoneId.systemDefault());

        Path p = Paths.get("timeZones.csv");
        try (BufferedWriter tzfile = Files.newBufferedWriter(p,
                StandardCharsets.US_ASCII)) {
            String header = String.format("%s,%s,%s,%s,%s,%s%n", "ZONEID", "OFFSET", "DT", "DATETIME", "SHORT_US", "FULL_US");
            tzfile.write(header);
            for (String s : zoneList) {
                ZoneId zone = ZoneId.of(s);
                ZonedDateTime zdt = zldt.withZoneSameInstant(zone);
                Instant instant = Instant.from(zdt);
                List<ZoneOffsetTransitionRule> tr = zone.getRules().getTransitionRules();
                Duration daylight = zone.getRules().getDaylightSavings(instant);

                String displayShort = zone.getDisplayName(TextStyle.SHORT, Locale.US);
                String displayNameUS = zone.getDisplayName(TextStyle.FULL, Locale.US);
                String dateTimeStr = zdt.format(DateTimeFormatter.ofPattern("MMM d yyyy  hh:mm a"));
                ZoneOffset offset = zdt.getOffset();

                String out = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n", zone, offset, daylight, dateTimeStr, displayShort, displayNameUS);
                // Write all time zones to the file.
                tzfile.write(out);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }
}
