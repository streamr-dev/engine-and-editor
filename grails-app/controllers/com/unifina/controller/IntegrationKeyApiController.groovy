package com.unifina.controller

import com.unifina.domain.IntegrationKey
import com.unifina.domain.User
import com.unifina.service.ApiException
import com.unifina.service.EthereumIntegrationKeyService
import com.unifina.service.NotFoundException
import grails.converters.JSON
import groovy.json.JsonSlurper

class IntegrationKeyApiController {
	EthereumIntegrationKeyService ethereumIntegrationKeyService

	@StreamrApi
	def index(IntegrationKeyListParams listParams) {
		def criteria = listParams.createListCriteria() << {
			eq("user", apiUser())
		}
		def integrationKeys = IntegrationKey.withCriteria(criteria)
		render(integrationKeys*.toMap() as JSON)
	}

	@StreamrApi
	def save(SaveIntegrationKeyCommand cmd) {
		switch (cmd.service as IntegrationKey.Service) {
			case IntegrationKey.Service.ETHEREUM_ID:
				IntegrationKey key = ethereumIntegrationKeyService.createEthereumID(apiUser(), cmd.name, cmd.challenge.id, cmd.challenge.challenge, cmd.signature)
				response.status = 201
				Map json = new JsonSlurper().parseText(key.json)
				render([
					id: key.id,
					challenge: [
						id: cmd.challenge.id,
						challenge: cmd.challenge.challenge
					],
					json: [
						address: json.address
					],
					name: cmd.name,
					service: IntegrationKey.Service.ETHEREUM_ID.toString(),
					signature: cmd.signature
				] as JSON)
				break
			default:
				throw new ApiException(400, 'INVALID_SERVICE', "Invalid service: $request.JSON.service")
		}
	}

	@StreamrApi
	def delete(String id) {
		ethereumIntegrationKeyService.delete(id, apiUser())
		render(status: 204, body: "")
	}

	@StreamrApi
	def update(String id) {
		Map json = new JsonSlurper().parseText((String) request.JSON)
		if (json.name == null || json.name.trim() == "") {
			throw new ApiException(400, "BODY_NOT_VALID", "must send JSON body containing property 'name'.")
		}
		String name = (String) json.name

		try {
			ethereumIntegrationKeyService.updateKey(apiUser(), id, name)
		} catch (NotFoundException e) {
			throw e
		}
		render(status: 204, body: "")
	}

	User apiUser() {
		request.apiUser
	}
}
