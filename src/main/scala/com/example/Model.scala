package com.example

class LogServerRepository {
  def lookup(logServerId: Int) : LogServer = LogServer(logServerId, "logServer" + logServerId)
}

case class LogServer(id: Int, name: String)