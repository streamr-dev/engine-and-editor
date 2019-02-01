package com.unifina.signalpath.utils

import com.streamr.client.protocol.message_layer.StreamMessageV30
import com.unifina.domain.data.Stream
import com.unifina.domain.security.SecUser
import grails.test.mixin.Mock
import spock.lang.Specification

@Mock(SecUser)
class MessageChainUtilSpec extends Specification {

	MessageChainUtil msgChainUtil = new MessageChainUtil()
	Map content = [foo: "bar"]
	Stream stream = new Stream()
	Long userId = 1
	def setup() {
		stream.id = "streamId"
		new SecUser(id: userId).save(failOnError: true, validate: false)
	}


	void "chains correctly messages with same timestamp"() {
		Date date = new Date()
		when:
		StreamMessageV30 msg1 = msgChainUtil.getStreamMessage(stream, date, content, userId)
		StreamMessageV30 msg2 = msgChainUtil.getStreamMessage(stream, date, content, userId)
		StreamMessageV30 msg3 = msgChainUtil.getStreamMessage(stream, date, content, userId)
		then:
		msg1.getStreamId() == stream.getId()
		msg1.getPublisherId() == "1"
		msg1.getTimestamp() == date.getTime()
		msg1.getSequenceNumber() == 0
		msg1.getPreviousMessageRef() == null
		msg1.getContent() == content
		msg2.getStreamId() == stream.getId()
		msg2.getPublisherId() == "1"
		msg2.getTimestamp() == date.getTime()
		msg2.getSequenceNumber() == 1
		msg2.getPreviousMessageRef().timestamp == date.getTime()
		msg2.getPreviousMessageRef().sequenceNumber == 0
		msg3.getStreamId() == stream.getId()
		msg3.getPublisherId() == "1"
		msg3.getTimestamp() == date.getTime()
		msg3.getSequenceNumber() == 2
		msg3.getPreviousMessageRef().timestamp == date.getTime()
		msg3.getPreviousMessageRef().sequenceNumber == 1
	}

	void "chains correctly messages with different timestamps"() {
		Date date1 = new Date()
		Date date2 = new Date(date1.getTime()+1000)
		Date date3 = new Date(date2.getTime()+1000)
		when:
		StreamMessageV30 msg1 = msgChainUtil.getStreamMessage(stream, date1, content, userId)
		StreamMessageV30 msg2 = msgChainUtil.getStreamMessage(stream, date2, content, userId)
		StreamMessageV30 msg3 = msgChainUtil.getStreamMessage(stream, date3, content, userId)
		then:
		msg1.getStreamId() == stream.getId()
		msg1.getPublisherId() == "1"
		msg1.getTimestamp() == date1.getTime()
		msg1.getSequenceNumber() == 0
		msg1.getPreviousMessageRef() == null
		msg1.getContent() == content
		msg2.getStreamId() == stream.getId()
		msg2.getPublisherId() == "1"
		msg2.getTimestamp() == date2.getTime()
		msg2.getSequenceNumber() == 0
		msg2.getPreviousMessageRef().timestamp == date1.getTime()
		msg2.getPreviousMessageRef().sequenceNumber == 0
		msg3.getStreamId() == stream.getId()
		msg3.getPublisherId() == "1"
		msg3.getTimestamp() == date3.getTime()
		msg3.getSequenceNumber() == 0
		msg3.getPreviousMessageRef().timestamp == date2.getTime()
		msg3.getPreviousMessageRef().sequenceNumber == 0
	}

	void "chains messages separately on different streams"() {
		Date date1 = new Date()
		Date date2 = new Date(date1.getTime()+1000)
		Date date3 = new Date(date2.getTime()+1000)
		Stream stream2 = new Stream()
		stream2.id = "streamId2"
		when:
		StreamMessageV30 msg1 = msgChainUtil.getStreamMessage(stream, date1, content, userId)
		StreamMessageV30 msg2 = msgChainUtil.getStreamMessage(stream2, date2, content, userId)
		StreamMessageV30 msg3 = msgChainUtil.getStreamMessage(stream, date3, content, userId)
		StreamMessageV30 msg4 = msgChainUtil.getStreamMessage(stream2, date2, content, userId)
		then:
		msg1.getStreamId() == stream.getId()
		msg1.getPublisherId() == "1"
		msg1.getTimestamp() == date1.getTime()
		msg1.getSequenceNumber() == 0
		msg1.getPreviousMessageRef() == null
		msg1.getContent() == content
		msg2.getStreamId() == stream2.getId()
		msg2.getPublisherId() == "1"
		msg2.getTimestamp() == date2.getTime()
		msg2.getSequenceNumber() == 0
		msg2.getPreviousMessageRef() == null
		msg3.getStreamId() == stream.getId()
		msg3.getPublisherId() == "1"
		msg3.getTimestamp() == date3.getTime()
		msg3.getSequenceNumber() == 0
		msg3.getPreviousMessageRef().timestamp == date1.getTime()
		msg3.getPreviousMessageRef().sequenceNumber == 0
		msg4.getStreamId() == stream2.getId()
		msg4.getPublisherId() == "1"
		msg4.getTimestamp() == date2.getTime()
		msg4.getSequenceNumber() == 1
		msg4.getPreviousMessageRef().timestamp == date2.getTime()
		msg4.getPreviousMessageRef().sequenceNumber == 0
	}
}
