package com.ke.bella.openapi.controller.login.session;

public interface TicketManager {
	void saveTicket(String ticket);

	boolean isValidTicket(String ticket);

	void removeTicket(String ticket);
}
