package com.unifina.controller.api

import com.unifina.api.ApiException
import com.unifina.domain.security.SecUser
import com.unifina.security.Challenge
import com.unifina.security.SessionToken
import com.unifina.security.AuthLevel
import com.unifina.security.StreamrApi
import com.unifina.service.ChallengeService
import com.unifina.service.EthereumIntegrationKeyService
import com.unifina.service.SessionService
import com.unifina.service.UserService
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["IS_AUTHENTICATED_ANONYMOUSLY"])
class LoginApiController {

	static allowedMethods = [challenge: "POST", response: "POST", password: "POST", apikey: "POST"]
	ChallengeService challengeService
	SessionService sessionService
	EthereumIntegrationKeyService ethereumIntegrationKeyService
	UserService userService

	@StreamrApi(authenticationLevel = AuthLevel.NONE)
	def challenge() {
		Challenge ch = challengeService.createChallenge()
		render(ch.toMap() as JSON)
	}

	@StreamrApi(authenticationLevel = AuthLevel.NONE)
	def response(ChallengeResponseCommand cmd) {
		boolean valid = challengeService.verifyChallengeResponse(cmd.challenge?.id,
			cmd.challenge?.challenge, cmd.signature.toLowerCase(), cmd.address.toLowerCase())
		if (!valid) {
			throw new ApiException(400, 'INVALID_CHALLENGE', "challenge-based login failed")
		} else {
			SecUser user = ethereumIntegrationKeyService.getOrCreateFromEthereumAddress(cmd.address)
			SessionToken token = sessionService.generateToken(user)
			render(token.toMap() as JSON)
		}
	}

	@StreamrApi(authenticationLevel = AuthLevel.NONE)
	def password(UsernamePasswordCommand cmd) {
		SecUser user = userService.getUserFromUsernameAndPassword(cmd.username, cmd.password)
		SessionToken token = sessionService.generateToken(user)
		render(token.toMap() as JSON)
	}

	@StreamrApi(authenticationLevel = AuthLevel.NONE)
	def apikey(ApiKeyCommand cmd) {
		SecUser user = userService.getUserFromApiKey(cmd.apiKey)
		SessionToken token = sessionService.generateToken(user)
		render(token.toMap() as JSON)
	}
}