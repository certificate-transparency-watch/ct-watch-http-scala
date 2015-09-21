package com.example


import com.google.common.base.Charsets

import scala.collection.JavaConversions._
import java.io.ByteArrayInputStream
import java.security.cert.{CertificateFactory, X509Certificate}

import org.bouncycastle.asn1.x500.style.{IETFUtils, BCStyle}
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
import org.joda.time.DateTime

case class Certificate(serialNumber: BigInt, commonName: String, subjectAlternativeNames: Option[Seq[Seq[_]]], expiry: DateTime, issuerDN: String)

class CertificateParser {

  def parse(c: Array[Byte]): Certificate = {
    val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
    val x509cert: X509Certificate = cf.generateCertificate(new ByteArrayInputStream(c)).asInstanceOf[X509Certificate]
    val cert = new JcaX509CertificateHolder(x509cert)
    val cn = IETFUtils.valueToString(cert.getSubject.getRDNs(BCStyle.CN)(0).getFirst.getValue)
    val issuerDN = IETFUtils.valueToString(cert.getIssuer.getRDNs(BCStyle.CN)(0).getFirst.getValue)
    val sans = Option(x509cert.getSubjectAlternativeNames).map { s => s.toSeq.map { i => i.toIndexedSeq }}
    Certificate(x509cert.getSerialNumber, cn, sans, new DateTime(cert.getNotAfter), issuerDN)
  }

  def parse(le: LogEntry): Certificate = parse(("-----BEGIN CERTIFICATE-----\n" + le.certificate + "\n-----END CERTIFICATE-----").getBytes(Charsets.US_ASCII))
}
