package com.example

import scala.collection.JavaConversions._
import java.io.ByteArrayInputStream
import java.security.cert.{CertificateFactory, X509Certificate}

import org.bouncycastle.asn1.x500.style.{IETFUtils, BCStyle}
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
import org.joda.time.DateTime

case class Certificate(serialNumber: BigInt, commonName: String, subjectAlternativeNames: Seq[String], expiry: DateTime)

class CertificateParser {

  def parse(c: Array[Byte]): Certificate = {
    val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
    val x509cert: X509Certificate = cf.generateCertificate(new ByteArrayInputStream(c)).asInstanceOf[X509Certificate]
    val cert = new JcaX509CertificateHolder(x509cert)
    val cn = IETFUtils.valueToString(cert.getSubject.getRDNs(BCStyle.CN)(0).getFirst.getValue)
    val sans = x509cert.getSubjectAlternativeNames.map { l: java.util.List[_] =>
      l.get(1).asInstanceOf[String]
    }.toSeq
    Certificate(x509cert.getSerialNumber, cn, sans, new DateTime(cert.getNotAfter))
  }
}
