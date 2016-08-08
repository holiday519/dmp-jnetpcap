package com.pxene.dmp.bean;

import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;

public class DpiData {
	private String srcIP;
	private String dstIP;
	private String srcPort;
	private String dstPort;
	private String method;
	private String host;
	private String url;
	private String referer;
	private String userAgent;
	private String cookie;
	private String contentType;
	private String content;
	private String date;
	
	private DpiData(String srcIP, String dstIP, String srcPort, String dstPort,
			String method, String host, String url, String referer,
			String userAgent, String cookie, String contentType,
			String content, String date) {
		this.srcIP = srcIP == null ? "" : srcIP;
		this.dstIP = dstIP == null ? "" : dstIP;
		this.srcPort = srcPort == null ? "" : srcPort;
		this.dstPort = dstPort == null ? "" : dstPort;
		this.method = method == null ? "" : method;
		this.host = host == null ? "" : host;
		this.url = url == null ? "" : url;
		this.referer = referer == null ? "" : referer;
		this.userAgent = userAgent == null ? "" : userAgent;
		this.cookie = cookie == null ? "" : cookie;
		this.contentType = contentType == null ? "" : contentType;
		this.content = content == null ? "" : content;
		this.date = date == null ? "" : date;
	}

	// 解析packet
	public static DpiData parsePacket(PcapPacket packet) {
		Ip4 ip = new Ip4();
		Tcp tcp = new Tcp();
		Http http = new Http();
		Payload payload = new Payload();
		
		if (!packet.hasHeader(ip) || !packet.hasHeader(tcp) || !packet.hasHeader(http) || http.isResponse()) {
			return null;
		}
		
		packet.getHeader(ip);
		packet.getHeader(tcp);
		packet.getHeader(http);
		packet.getHeader(payload);
		
		return new DpiData(FormatUtils.ip(ip.source()), FormatUtils.ip(ip.destination()), 
				String.valueOf(tcp.source()), String.valueOf(tcp.destination()),
				http.fieldValue(Http.Request.RequestMethod), http.fieldValue(Http.Request.Host),
				http.fieldValue(Http.Request.RequestUrl), http.fieldValue(Http.Request.Referer),
				http.fieldValue(Http.Request.User_Agent), http.fieldValue(Http.Request.Cookie),
				http.fieldValue(Http.Request.Content_Type), packet.hasHeader(payload) ? new String(payload.data()) : null,
				http.fieldValue(Http.Request.Date));
	}

	public String getSrcIP() {
		return srcIP;
	}

	public void setSrcIP(String srcIP) {
		this.srcIP = srcIP;
	}

	public String getDstIP() {
		return dstIP;
	}

	public void setDstIP(String dstIP) {
		this.dstIP = dstIP;
	}

	public String getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}

	public String getDstPort() {
		return dstPort;
	}

	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}