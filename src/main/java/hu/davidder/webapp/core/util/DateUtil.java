package hu.davidder.webapp.core.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import hu.davidder.webapp.core.base.order.entity.Order;

@Service
public class DateUtil {
	
	@Value("${FILTER_INTERVAL:1m}")
	private String filterInterval;
	

	public boolean isWithinDateRange(Order order, ZonedDateTime from, ZonedDateTime to) {
        ZonedDateTime orderDate = order.getShipDate();
        return (from == null || !orderDate.isBefore(from)) &&
               (to == null || !orderDate.isAfter(to));
    }
	

	public ZonedDateTime parseZonedDateTime(String dateTimeString) {
		if (dateTimeString == null || dateTimeString.isEmpty()) {
			return null;
		}
		try {
			return ZonedDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
					.withZoneSameInstant(ZoneId.of("UTC"));
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid date-time format: " + dateTimeString, e);
		}
	}
	
	public long parseInterval() {
        char unit = filterInterval.charAt(filterInterval.length() - 1);
        long value = Long.parseLong(filterInterval.substring(0, filterInterval.length() - 1));
        switch (unit) {
            case 'm': return value * 30; // Months
            case 'd': return value; // Days
            default: throw new IllegalArgumentException("Invalid interval unit");
        }
    }
	
}
