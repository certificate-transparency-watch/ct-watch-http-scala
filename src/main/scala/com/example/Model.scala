package com.example

class LogServerRepository {
  def lookup(logServerId: Int) : LogServer = LogServer(logServerId, "logServer" + logServerId)
}

case class LogServer(id: Int, name: String)

case class SignedTreeHead(
    treesize : Long,
    timestamp: Long,
    rootHash: String,
    treeHeadSignature: String,
    verified: Boolean,
    logserverId: Int
)

case class LogEntry (
    logServer: String,
    idx: Long,
    domain: String,
    certificate: String
)

trait LogEntryRepository {
  def lookupByDomain(domain: String): Seq[LogEntry]
}

trait SignedTreeHeadRepository {
  val findByLogServerName : String => Option[Seq[SignedTreeHead]]
}

