package com.unifina.service

import com.unifina.domain.marketplace.Category
import com.unifina.domain.marketplace.Product
import com.unifina.domain.security.SecUser
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(ProductService)
@Mock([Product, SecUser, Category])
class RelatedProductsSpec extends Specification {
	Product newProduct(String id, String name, String description, Category c, SecUser user) {
		Product p = new Product(
			name: name,
			description: description,
			imageUrl: "http://www.img.com/foo.jpg",
			thumbnailUrl: "http://www.img.com/foo.jpg",
			category: c,
			dateCreated: new Date(),
			lastUpdated: new Date(),
			score: 0,
			owner: user,
			ownerAddress: null,
			beneficiaryAddress: null,
			pricePerSecond: 0,
			minimumSubscriptionInSeconds: 0,
			blockNumber: 0,
			blockIndex: 0
		)
		p.id = id
		p.save(validate: true, failOnError: true)
		return p
	}

	Product p1, p2, p3, p4, p5, p6
	SecUser u1, u2
	Category cat1, cat2

	void setup() {
		// u1 is the user who owns the product p1 used to search for related products
		u1 = new SecUser(
			username: "username: masa@hypätääneka.com",
			name: "Matti Nykänen",
			password: "xxx"
		)
		u1.id = 1
		u1.save(validate: false, failOnError: true)

		u2 = new SecUser(
			username: "username: marilyn@monroe.com",
			name: "Marilyn Monroe",
			password: "xxx"
		)
		u2.id = 2
		u2.save(validate: false, failOnError: true)

		cat1 = new Category(name: "Car Category")
		cat1.id = "car-category"
		cat1.save(validate: true, failOnError: true)

		// p1 is the product used to search for related products
		p1 = newProduct("p1-id", "car data", "data of cars", cat1, u1)
		p2 = newProduct("p2-id", "F1 car data", "data of F1 cars", cat1, u2)
		p5 = newProduct("p5-id", "Rally car data", "data of rally cars", cat1, u2)

		cat2 = new Category(name: "Teapot Category")
		cat2.id = "teapot-category"
		cat2.save(validate: true, failOnError: true)

		p3 = newProduct("p3-id", "Teapot data", "data of teapots", cat2, u1)
		p4 = newProduct("p4-id", "Teapot data 2", "data of teapots 2", cat2, u2)
		p6 = newProduct("p6-id", "Teapot data 3", "data of teapots 3", cat2, u2)
		// results should list:
		//   - p2 (same category)
		//   - p3 (same user, but different category)
		//   - p5 (same category)
		//
		// results should not list:
		//   - p1 (product used to find related products)
		//   - p4 (different category and user)
		//   - p6 (different category and user)

		// UI is specified to show three (3) related products
	}

	void "find related products"() {
		when:
		def max = 3
		def products = service.relatedProducts(p1, max)
		then:
		products.size() == 3
		products.contains(p1) == false
		products.contains(p2) == true
		products.contains(p3) == true
		products.contains(p5) == true
	}

	void "find related products with non existing id"() {
		when:
		def products = service.relatedProducts(null, 3)
		then:
		products.size() == 0
	}

}