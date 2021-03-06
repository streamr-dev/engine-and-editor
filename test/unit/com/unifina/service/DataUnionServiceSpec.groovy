package com.unifina.service


import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.gaul.httpbin.HttpBin
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DataUnionServiceSpec extends Specification {
	private URI httpBinEndpoint = URI.create("http://127.0.0.1:0")
	private final HttpBin httpBin = new HttpBin(httpBinEndpoint)
	private DataUnionService service

	void setup() {
		Logger.getRootLogger().setLevel(Level.OFF)
		Logger.getLogger(DataUnionService.class).setLevel(Level.OFF)
		httpBin.start()
		httpBinEndpoint = new URI(
			httpBinEndpoint.getScheme(),
			httpBinEndpoint.getUserInfo(),
			httpBinEndpoint.getHost(),
			httpBin.getPort(),
			httpBinEndpoint.getPath(),
			httpBinEndpoint.getQuery(),
			httpBinEndpoint.getFragment()
		)
		service = new DataUnionService()
		service.grailsApplication = getGrailsApplication()
	}

	void cleanup() {
		service.close()
		httpBin.stop()
	}

	void "test execute"() {
		setup:
		service.afterPropertiesSet()
		String url = httpBinEndpoint.toString() + "/stream/1"
		String expected = """{"args":{},"headers":{"Accept":"application/json","Connection":"keep-alive","User-Agent"""
		when:
		DataUnionService.ProxyResponse result = service.proxy(url)
		then:
		result.body.startsWith(expected)
		result.statusCode == 200
	}

	void "test execute returns 400"() {
		setup:
		service.afterPropertiesSet()
		String url = httpBinEndpoint.toString() + "/status/400"
		when:
		DataUnionService.ProxyResponse result = service.proxy(url)
		then:
		result.body == ""
		result.statusCode == 400
	}

	void "test server not responding"() {
		setup:
		service.afterPropertiesSet()
		DataUnionProxyException e
		when:
		try {
			service.proxy("http://localhost:1")
		} catch (DataUnionProxyException err) {
			e = err
		}
		then:
		e.message == "Data Union server is busy or not responding"
		e.code == "PROXY_ERROR"
		e.statusCode == 503
		e.extraHeaders == ["Retry-After": "60"]
	}

	void "test execute returns 404"() {
		setup:
		service.afterPropertiesSet()
		String url = httpBinEndpoint.toString() + "/status/404"
		when:
		DataUnionService.ProxyResponse result = service.proxy(url)
		then:
		result.body == ""
		result.statusCode == 404
	}

	void "test execute returns 500"() {
		setup:
		service.afterPropertiesSet()
		String url = httpBinEndpoint.toString() + "/status/500"
		when:
		DataUnionService.ProxyResponse result = service.proxy(url)
		then:
		result.body == ""
		result.statusCode == 500
	}

	static class Runner implements Runnable {
		DataUnionService service
		String url
		DataUnionService.ProxyResponse response

		Runner(DataUnionService service, String url) {
			this.service = service
			this.url = url
		}

		@Override
		void run() {
			response = service.proxy(url)
		}
	}

	void "test connection pool is configured"() {
		setup:
		service.afterPropertiesSet()
		String url = httpBinEndpoint.toString() + "/delay/4"
		int size = 10

		when:
		List<Runner> results = new ArrayList<>()
		ExecutorService executor = Executors.newFixedThreadPool(size);
		for (int i = 0; i < size; i++) {
			Runner runner = new Runner(service, url)
			results.add(runner)
			executor.execute(runner)
		}
		executor.shutdown()
		while (!executor.isTerminated()) {}
		then:
		for (int i = 0; i < size; i++) {
			assert results.get(i).response.statusCode == 200
		}
	}

	void "test socket read timeout"() {
		setup:
		grailsApplication.config.streamr.cps.connectTimeout = 1000
		grailsApplication.config.streamr.cps.connectionRequestTimeout = 5000
		grailsApplication.config.streamr.cps.socketTimeout = 1000
		service.afterPropertiesSet()
		String url = httpBinEndpoint.toString() + "/delay/3"
		when:
		service.proxy(url)

		then:
		def e = thrown(DataUnionProxyException)
		e.getStatusCode() == 504
		e.getMessage() == "Data Union server gateway timeout"
	}
}
