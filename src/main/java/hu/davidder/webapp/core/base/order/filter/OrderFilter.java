package hu.davidder.webapp.core.base.order.filter;

import java.time.ZonedDateTime;

public class OrderFilter {
	private ZonedDateTime from;
	private ZonedDateTime to;

	public ZonedDateTime getFrom() {
		return from;
	}

	public void setFrom(ZonedDateTime from) {
		this.from = from;
	}

	public ZonedDateTime getTo() {
		return to;
	}

	public void setTo(ZonedDateTime to) {
		this.to = to;
	}
}