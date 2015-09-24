package com.example

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result
import com.codahale.metrics.health.HealthCheckRegistry
import com.google.common.collect.{MapDifference, Maps}
import net.noerd.prequel.DatabaseConfig
import org.joda.time.DateTime

class HealthChecks(healthCheckRegistry: HealthCheckRegistry, db: DatabaseConfig) {
  healthCheckRegistry.register("recent sth", new RecentSthCheck(db))
  healthCheckRegistry.register("unprocessed log entries", new UnprocessedLogEntriesCheck(db))
  healthCheckRegistry.register("unverified sth", new UnverifiedSthCheck(db))
  healthCheckRegistry.register("sth drift", new SthDrift(db))
}

class RecentSthCheck(db: DatabaseConfig) extends HealthCheck {
  override def check(): Result = {
    val results: Seq[DateTime] = db.transaction { tx =>
      tx.select("""
WITH RECURSIVE  t AS (
    SELECT max(log_server_id) AS log_server_id FROM sth
    UNION ALL
    SELECT (SELECT max(log_server_id) as log_server_id FROM sth WHERE log_server_id < t.log_server_id)
        FROM t where t.log_server_id is not null
)
select (select max(timestamp) from sth where log_server_id=t.log_server_id) from t where t.log_server_id is not null order by log_server_id;""") { r =>
        new DateTime(r.nextLong.get)
      }
    }

    if (results.length != 8) {
      Result.unhealthy("There exists a log server that has 0 STHs")
    } else {
      if (results.forall { datetime => datetime.isAfter(new DateTime().minusDays(1)) })
        Result.healthy()
      else
        Result.unhealthy("There exists a log server whose latest STH is >3 hours")
    }
  }

}

class UnprocessedLogEntriesCheck(db: DatabaseConfig) extends HealthCheck {
  override def check(): Result = {
    val numberOfUnprocessedLogEntries: Int = db.transaction { tx =>
      tx.selectInt("select count(*) from log_entry where domain is null")
    }

    if (numberOfUnprocessedLogEntries > 100000) {
      Result.unhealthy("There are " + numberOfUnprocessedLogEntries + " unprocessed log entries.")
    } else {
      Result.healthy()
    }
  }
}

class UnverifiedSthCheck(db: DatabaseConfig) extends HealthCheck {
  override def check(): Result = {
    val numberOfUnverifiedSths: Seq[Int] = db.transaction { tx =>
      tx.select("select count(*) from sth where verified = false group by log_server_id") { r =>
        r.nextInt.get
      }
    }

    if (numberOfUnverifiedSths.exists { _ > 1 })
      Result.unhealthy("There exists a log server with more than one unverified STH")
    else
      Result.healthy()
  }
}

class SthDrift(db: DatabaseConfig) extends HealthCheck {
  override def check(): Result = {
    type Id = Int
    val sthTreesizes : Map[Id, Int] = seqTupleToMap(db.transaction { tx =>
      tx.select("""
WITH RECURSIVE  t AS (
    SELECT max(log_server_id) AS log_server_id FROM sth
    UNION ALL
    SELECT (SELECT max(log_server_id) as log_server_id FROM sth WHERE log_server_id < t.log_server_id)
        FROM t where t.log_server_id is not null
) select log_server_id, (select max(treesize) from sth where log_server_id=t.log_server_id) from t where t.log_server_id is not null order by log_server_id;""") { r =>
        (r.nextInt.get, r.nextInt.get)
      }
    })

    val logEntriesIndexes: Map[Id, Int] = seqTupleToMap(db.transaction { tx =>
      tx.select("WITH RECURSIVE  t AS" +
        "(SELECT max(log_server_id) AS log_server_id FROM log_entry UNION ALL SELECT (SELECT max(log_server_id) as log_server_id FROM log_entry WHERE log_server_id < t.log_server_id) FROM t where t.log_server_id is not null)" +
        "select log_server_id, (select max(idx) from log_entry where log_server_id=t.log_server_id) from t where t.log_server_id is not null order by log_server_id;") { r =>
        (r.nextInt.get, r.nextInt.get)
      }
    })

    import scala.collection.JavaConverters._
    val diff: MapDifference[Id, Int] = Maps.difference(sthTreesizes.asJava, logEntriesIndexes.asJava)

    if (sthTreesizes.size != logEntriesIndexes.size)
      Result.unhealthy("Some log servers have no log entries")
    else if (diff.entriesDiffering().asScala.exists { case (a,b) => Math.abs(b.leftValue - b.rightValue) > 15000 })
      Result.unhealthy("Log entries indexes and STH tree size have drifted, for at least one log server: " + diff.entriesDiffering().asScala.toString)
    else
      Result.healthy()
  }

  private def seqTupleToMap(in: Seq[(Int, Int)]): Map[Int, Int] = in.groupBy(_._1).map { case (k, v) => (k, v.map(_._2).head) }
}

class MaximumMergeDelayCheck extends HealthCheck {
  override def check(): Result = throw new NotImplementedError()
}
