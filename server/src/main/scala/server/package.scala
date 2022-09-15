package object server:

  sealed class StreamError(message: String) extends Exception(message)

  case class HttpError(message: String) extends StreamError(message)

  case class HttpTlsError(message: String) extends StreamError(message)

  sealed class WebRiskError(message: String) extends Exception(message)
