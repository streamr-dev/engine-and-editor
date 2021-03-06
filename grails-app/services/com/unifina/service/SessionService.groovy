package com.unifina.service

import com.unifina.domain.User
import com.unifina.domain.Userish
import org.apache.log4j.Logger

import java.time.Instant
import java.time.temporal.ChronoUnit

class SessionService {
	static final int TOKEN_LENGTH = 64
	static final int TTL_HOURS = 3

	KeyValueStoreService keyValueStoreService

	private static final Logger log = Logger.getLogger(SessionService)

	void updateUsersLoginDate(User user, Date date) {
		// Using update query to avoid StaleObjectStateException in case of concurrent logins.
		// Not unit testable, but there's coverage in end-to-end tests.
		User.executeUpdate("update User u set u.lastLogin = ? where u.id = ?", [date, user.id])
	}

	SessionToken generateToken(Userish userish) {
		SessionToken sk = new SessionToken(TOKEN_LENGTH, userish, TTL_HOURS)
		keyValueStoreService.setWithExpiration(sk.getToken(), userishToString(userish), TTL_HOURS * 3600)
		if (userish instanceof User) {
			updateUsersLoginDate((User) userish, new Date())
		}
		return sk
	}

	Userish getUserishFromToken(String token) throws InvalidSessionTokenException {
		Date date = Date.from(Instant.now().plus(TTL_HOURS, ChronoUnit.HOURS))
		keyValueStoreService.resetExpiration(token, date)
		String userishClassAndId = keyValueStoreService.get(token)

		if (userishClassAndId == null) {
			throw new InvalidSessionTokenException("Invalid token: " + token)
		}
		return stringToUserish(userishClassAndId)
	}

	void invalidateSession(String sessionToken) {
		keyValueStoreService.delete(sessionToken)
	}

	String userishToString(Userish u) {
		if (u instanceof User) {
			return "User" + u.id.toString()
		}
		throw new InvalidArgumentsException("Unrecognized userish")
	}

	Userish stringToUserish(String s) {
		if (s.startsWith("User")) {
			String id = s.substring(4)
			return User.get(id)
		} else if (s.startsWith("SecUser")) {
			String id = s.substring(7)
			return User.get(id)
		} else {
			throw new InvalidArgumentsException("Unrecognized string")
		}
	}

}
