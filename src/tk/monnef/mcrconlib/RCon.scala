package tk.monnef.mcrconlib

import scala.util.Random
import java.net.{UnknownHostException, Socket}
import java.io.{IOException, OutputStream, InputStream}
import java.nio.charset.StandardCharsets
import java.nio.{ByteOrder, ByteBuffer}

/**
 * @author monnef
 * @note Based on vincent's code - https://code.google.com/p/rcon-client/
 *       Protocol format - http://wiki.vg/Rcon
 */
class RCon() {

  import RCon._

  private var requestId: Int = _
  private val sync = new Object
  private var socket: Socket = _
  private var input: InputStream = _
  private var output: OutputStream = _
  private var _connected = false

  def connected = _connected

  def connect(host: String, port: Int, password: String): Unit = {
    wrapExceptions {
      requestId = Random.nextInt()
      socket = new Socket(host, port)
      output = socket.getOutputStream
      input = socket.getInputStream
      val resp = sendImpl(PACKET_TYPE_LOGIN, password.map {_.toByte}.toArray)
      if (resp.length != 0) protocolError("Expected a response of zero length.")
      _connected = true
    }
  }

  override def toString: String = s"RCon [requestId=$requestId, socket=$socket, connected=$connected]"

  def send(payload: String): String = {
    ensureConnected()
    sendImpl(payload)
  }

  def close() {
    wrapExceptions {
      sync.synchronized {
        if (!socket.isClosed) {
          socket.close()
          _connected = false
        }
      }
    }
  }

  private def wrapExceptions[R](code: => R): R = {
    try {
      code
    } catch {
      case ioe: IOException => throw new RConException(ioe)
      case uhe: UnknownHostException => throw new RConException(uhe)
    }
  }

  private def sendImpl(packetType: Int, payload: Array[Byte]): Array[Byte] = {
    wrapExceptions {
      sync.synchronized {
        def sendCommand() {
          val len = PACKET_LEN_REQUEST_ID + PACKET_LEN_TYPE + payload.length + PACKET_LEN_PAD
          val toSend = new Array[Byte](PACKET_LEN_LENGTH + len)
          val sendBuffer = ByteBuffer.wrap(toSend)
          sendBuffer.order(ByteOrder.LITTLE_ENDIAN)
          sendBuffer.putInt(len)
          sendBuffer.putInt(requestId)
          sendBuffer.putInt(packetType)
          sendBuffer.put(payload)
          sendBuffer.put(zeroByte).put(zeroByte)
          output.write(toSend)
          output.flush()
        }
        def receiveResponse(): Array[Byte] = {
          val recBytes = new Array[Byte](MAX_RESPONSE_SIZE)
          val recBytesLen = input.read(recBytes)
          val recBuffer = ByteBuffer.wrap(recBytes, 0, recBytesLen)
          recBuffer.order(ByteOrder.LITTLE_ENDIAN)
          val recLen = recBuffer.getInt
          val recReqId = recBuffer.getInt
          val recType = recBuffer.getInt
          val recPayload = new Array[Byte](recLen - PACKET_LEN_REQUEST_ID - PACKET_LEN_TYPE - PACKET_LEN_PAD)
          recBuffer.get(recPayload)
          recBuffer.getShort // padding
          if (recReqId != requestId) throw new IncorrectRequestIdException(recReqId)
          recPayload
        }
        sendCommand()
        receiveResponse()
      }
    }
  }

  private def sendImpl(packetType: Int, payload: String): String = {
    new String(sendImpl(packetType, payload.getBytes(StandardCharsets.US_ASCII)), StandardCharsets.US_ASCII)
  }

  private def sendImpl(payload: String): String = {
    sendImpl(PACKET_TYPE_COMMAND, payload)
  }

  private def ensureConnected() {
    if (!connected) throw new NotConnected
  }
}

object RCon {
  private val PACKET_TYPE_COMMAND = 2
  private val PACKET_TYPE_LOGIN = 3
  private val zeroByte: Byte = 0
  private val MAX_RESPONSE_SIZE = 2048

  private val PACKET_LEN_LENGTH = 4
  private val PACKET_LEN_REQUEST_ID = 4
  private val PACKET_LEN_TYPE = 4
  private val PACKET_LEN_PAD = 2

  val DEFAULT_PORT = 25575

  class RConException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause) {
    def this(cause: Throwable) = this(null, cause)
  }

  class AuthenticationException(message: String = null, cause: Throwable = null) extends RConException(message, cause)

  class IncorrectRequestIdException(id: Int) extends AuthenticationException(s"Request id: $id")

  class ProtocolException(message: String) extends RConException(message)

  class NotConnected extends RConException

  def protocolError(message: String) { throw new ProtocolException(message) }
}