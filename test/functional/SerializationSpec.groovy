import com.unifina.controller.core.signalpath.CanvasController
import com.unifina.kafkaclient.UnifinaKafkaProducer
import com.unifina.service.SerializationService
import com.unifina.utils.MapTraversal
import core.LoginTester1Spec
import core.mixins.CanvasMixin
import core.mixins.ConfirmationMixin
import grails.test.mixin.TestFor
import grails.util.Holders
import spock.lang.Shared

@Mixin(CanvasMixin)
@Mixin(ConfirmationMixin)
@TestFor(CanvasController) // makes grailsApplication available
class SerializationSpec extends LoginTester1Spec {

	static UnifinaKafkaProducer kafka

	@Shared long serializationIntervalInMillis

	def setupSpec() {
		kafka = new UnifinaKafkaProducer(makeKafkaConfiguration())

		// For some reason the annotations don't work so need the below.
		SerializationSpec.metaClass.mixin(CanvasMixin)
		SerializationSpec.metaClass.mixin(ConfirmationMixin)

		serializationIntervalInMillis = MapTraversal.getLong(Holders.config, SerializationService.INTERVAL_CONFIG_KEY)
	}

	def cleanupSpec() {
		synchronized(kafka) {
			kafka.close()
		}
	}

	def "resuming paused live canvas retains modules' states"() {
		String canvasName = "SerializationSpec" + new Date().getTime()

		when: "Modules are added and canvas started"
			// The stream
			searchAndClick("SerializationSpec")
			moduleShouldAppearOnCanvas("Stream")
			searchAndClick("Count")
			moduleShouldAppearOnCanvas("Count")
			searchAndClick("Sum")
			moduleShouldAppearOnCanvas("Sum")
			searchAndClick("Add")
			moduleShouldAppearOnCanvas("Add")
			searchAndClick("Label")
			moduleShouldAppearOnCanvas("Label")

			connectEndpoints(findOutput("Stream", "a"), findInput("Count", "in"))
			connectEndpoints(findOutput("Stream", "b"), findInput("Sum", "in"))
			connectEndpoints(findOutput("Count", "count"), findInput("Add", "in1"))
			connectEndpoints(findOutput("Sum", "out"), findInput("Add", "in2"))
			connectEndpoints(findOutput("Add", "sum"), findInput("Label", "label"))

			ensureRealtimeTabDisplayed()
			setCanvasName(canvasName)
			startCanvas(true)

		then: "Button is in correct state"
			runRealtimeButton.text().contains("Stop")

		when: "Data is sent"
			noNotificationsVisible()
			Thread.start {
				for (int i = 0; i < 20; ++i) {
					kafka.sendJSON("mvGKMdDrTeaij6mmZsQliA",
						"", System.currentTimeMillis(),
						'{"a":' + i + ', "b": ' + (i * 0.5)  + '}')
					sleep(150)
				}
			}
		then: "Label should show correct value"
			// Wait for enough data, sometimes takes more than 30 sec to come
			waitFor(30) { $(".modulelabel").text().toDouble() == 115.0D }
			sleep(serializationIntervalInMillis + 200)
			def oldVal = $(".modulelabel").text().toDouble()

		when: "Live canvas is stopped"
			noNotificationsVisible()
			stopCanvas()

		and: "Started again"
			noNotificationsVisible()
			startCanvas(false) // saving would reset serialized state
			noNotificationsVisible()
		and: "Data is sent"
			Thread.start {
				for (int i = 100; i < 105; ++i) {
					kafka.sendJSON("mvGKMdDrTeaij6mmZsQliA",
						"", System.currentTimeMillis(),
						'{"a":' + i + ', "b": ' + (i * 0.5)  + '}')
					sleep(150)
				}
			}
		then: "Label must change"
			waitFor(30){ $(".modulelabel").text().toDouble() == (oldVal + 5 + 255).toDouble()}

		when: "Live canvas is stopped"
			stopCanvas()
		and: "canvas started with 'reset' setting"
			noNotificationsVisible()
			resetAndStartCanvas(true)
			noNotificationsVisible()
		and: "Data is sent"
			Thread.start {
				for (int i = 0; i < 20; ++i) {
					kafka.sendJSON("mvGKMdDrTeaij6mmZsQliA",
						"", System.currentTimeMillis(),
						'{"a":' + i + ', "b": ' + (i * 0.5)  + '}')
					sleep(150)
				}
			}
		then: "Label must show correct value"
			// Wait for enough data, sometimes takes more than 30 sec to come
			waitFor(30) { $(".modulelabel").text().toDouble() == 115.0D }

		cleanup:
			stopCanvasIfRunning()
	}

	private def makeKafkaConfiguration() {
		Map<String,Object> kafkaConfig = MapTraversal.flatten((Map) MapTraversal.getMap(grailsApplication.config, "unifina.kafka"));
		Properties properties = new Properties();
		for (String s : kafkaConfig.keySet()) {
			properties.setProperty(s, kafkaConfig.get(s).toString());
		}
		return properties;
	}
}
