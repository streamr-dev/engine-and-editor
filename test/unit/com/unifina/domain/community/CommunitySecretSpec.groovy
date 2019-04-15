package com.unifina.domain.community

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(CommunitySecret)
class CommunitySecretSpec extends Specification {
	CommunitySecret sec

    def setup() {
		sec = new CommunitySecret(
			id: "1",
			name: "Name of the community",
			secret: "xxxxxxx",
			communityAddress: "0x0123456789abcdefABCDEF000000000000000000",
		)
    }

	void "valid CommunitySecret validates ok"() {
		when:
		def result = sec.validate()
		then:
		result
	}

	void "name cannot be null"() {
		when:
		sec.name = null
		def result = sec.validate()
		then:
		!result
	}

	void "secret cannot be null"() {
		when:
		sec.secret = null
		def result = sec.validate()
		then:
		!result
	}

	void "communityAddress must be an Ethereum address"() {
		when:
		sec.communityAddress = "x"
		def result = sec.validate()
		then:
		!result
	}

	void "communityAddress cannot be null"() {
		when:
		sec.communityAddress = null
		def result = sec.validate()
		then:
		!result
	}
}
