package com.unifina.signalpath.remote

import com.unifina.data.FeedEvent
import com.unifina.datasource.DataSource
import com.unifina.datasource.DataSourceEventQueue
import com.unifina.utils.Globals
import com.unifina.utils.testutils.ModuleTestHelper
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import spock.lang.Specification

class MqttSpec extends Specification {
	Mqtt module

	def setup() {
		module = new TestableMqtt()
		module.init()
	}

	def mockClient = Stub(MqttClient) {

	}

	/** Mocked event queue. Works manually in tests, please call module.receive(queuedEvent) */
	def mockGlobals = Stub(Globals) {
		getDataSource() >> Stub(DataSource) {
			getEventQueue() >> Stub(DataSourceEventQueue) {
				enqueue(_) >> { feedEventList ->
					event = feedEventList[0]
				}
			}
		}
		isRealtime() >> true
	}
	FeedEvent event

	void "module outputs the messages"() {
		TestableMqtt.mqttClient = mockClient
		module.setGlobals(mockGlobals)

		def collector = new ModuleTestHelper.Collector()
		collector.init()
		collector.attachToOutput(module.outputs.find { it.name == "message" })

		String topic = "topic"
		String msg = "message"

		when:
		module.initialize()
		module.onStart()
		module.messageArrived(topic, new MqttMessage(msg.getBytes()))
		module.receive(event)
		module.onStop()

		then:
		collector.inputs[0].value == msg
	}

	void "module re-throws a mqtt starting error"() {
		TestableMqtt.startingException = new MqttException(new Exception("Testing"))

		when:
		module.initialize()
		module.onStart()

		then:
		thrown RuntimeException
	}

}
