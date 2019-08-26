package akka.http.websocket.stomp.bus.event

import java.util.UUID

import akka.http.websocket.stomp.parser.{MessageFrame, SendFrame, StompHeader}

case class MessageEvent(destination: String,
                        headers: Seq[StompHeader],
                        body: Option[String],
                        user: Option[String]) {

  def frame: MessageFrame = MessageFrame(headers, body)

  def withSubscriptionId(id: String): MessageEvent = copy(headers = headers :+ StompHeader("subscription", id))
}

object MessageEvent {
  def apply(frame: SendFrame, user: Option[String] = None): MessageEvent = {
    val ct = frame.getHeader("content-type") match {
      case Some(h) => StompHeader("content-type", h.value)
      case None => StompHeader("content-type", "text/plain")
    }
    val ch = frame.getHeader("destination") match {
      case Some(h) => StompHeader("destination", h.value)
      case None => StompHeader("destination", "")
    }
    val id = StompHeader("message-id", UUID.randomUUID().toString)

    val headers = Seq(ct, id, ch)

    MessageEvent(ch.value, headers, frame.body, user)
  }
}
