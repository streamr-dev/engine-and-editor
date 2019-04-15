package com.unifina.domain.community

import com.unifina.domain.marketplace.Product
import com.unifina.utils.HexIdGenerator
import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString

@ToString
class CommunitySecret {
	String id
	// name to display for users.
	String name
	// secret shared by the community that enables automatic join.
	String secret
	// communityAddres is an Ethereum address of the community.
	String communityAddress

    static constraints = {
		name(nullable: false)
		secret(nullable: false)
		communityAddress(nullable: false, validator: Product.isEthereumAddress)
    }
	static mapping = {
		id generator: HexIdGenerator.name
	}

	@GrailsCompileStatic
	Map toMap() {
		return [
			id: id,
			name: name,
			communityAddress: communityAddress,
		]
	}
}
