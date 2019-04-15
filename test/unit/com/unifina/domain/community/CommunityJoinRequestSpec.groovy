package com.unifina.domain.community

import com.unifina.domain.security.SecUser
import grails.test.mixin.TestFor
import spock.lang.Specification


@TestFor(CommunityJoinRequest)
class CommunityJoinRequestSpec extends Specification {
	CommunityJoinRequest req
	SecUser me

    def setup() {
		me = new SecUser(
			id: "1",
			username: "email@address.com",
			password: "123",
			name: "Streamr User",
		)
		req = new CommunityJoinRequest(
			id: "1",
			user: me,
			memberAddress: "0xfffFFffFfffFffffFFFFffffFFFFfffFFFFfffFf",
			communityAddress: "0x0123456789abcdefABCDEF000000000000000000",
			state: CommunityJoinRequest.State.PENDING,
			dateCreated: new Date(),
			lastUpdated: new Date(),
		)
    }

	void "valid CommunityJoinRequest validates ok"() {
		setup:
		req.dateCreated = new Date()
		req.lastUpdated = new Date()
		when:
		def result = req.validate()
		then:
		result
	}

	void "user cannot be null"() {
		setup:
		req.dateCreated = new Date()
		req.lastUpdated = new Date()
		when:
		req.user = null
		def result = req.validate()
		then:
		!result
	}

    void "memberAddress must be an Ethereum address"() {
		setup:
		req.dateCreated = new Date()
		req.lastUpdated = new Date()
		when:
		req.memberAddress = "x"
		def result = req.validate()
		then:
		!result
    }

	void "memberAddress cannot be null"() {
		setup:
		req.dateCreated = new Date()
		req.lastUpdated = new Date()
		when:
		req.memberAddress = null
		def result = req.validate()
		then:
		!result
	}

	void "communityAddress must be an Ethereum address"() {
		setup:
		req.dateCreated = new Date()
		req.lastUpdated = new Date()
		when:
		req.communityAddress = "x"
		def result = req.validate()
		then:
		!result
	}

	void "communityAddress cannot be null"() {
		setup:
		req.dateCreated = new Date()
		req.lastUpdated = new Date()
		when:
		req.communityAddress = null
		def result = req.validate()
		then:
		!result
	}

	void "state cannot be null"() {
		setup:
		req.dateCreated = new Date()
		req.lastUpdated = new Date()
		when:
		req.state = null
		def result = req.validate()
		then:
		!result
	}
}
