package com.pxene.dmp.test;

import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Http.Response;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

public class PacketMatch {

	private static PacketMatch pm;
	private Ip4 ip = new Ip4();
	private Icmp icmp = new Icmp();
	private Tcp tcp = new Tcp();
	private Udp udp = new Udp();

	final Http http = new Http();

	public static PacketMatch getInstance() {
		if (pm == null) {
			pm = new PacketMatch();
		}
		return pm;
	}

	public void handlePacket(PcapPacket packet) {// 根据包头选择不同的规则
		if (packet.hasHeader(ip)) {
			handleIp(packet);
		}
		if (packet.hasHeader(icmp)) {
			handleIcmp(packet);
		}
		if (packet.hasHeader(tcp)) {
			handleTcp(packet);
		}
		if (packet.hasHeader(udp)) {
			handleUdp(packet);
		}
		if (packet.hasHeader(http) && packet.hasHeader(tcp)) {
			handleHttp(packet);
		}
	}

	private void handleIp(PcapPacket packet) {
		packet.getHeader(ip);
		byte[] sIP = new byte[4], dIP = new byte[4];
		sIP = ip.source();
		dIP = ip.destination();
		String srcIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
		String dstIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
		System.out.println("*****srcIP******" + srcIP);
		System.out.println("*****dstIP******" + dstIP);
	}

	private void handleIcmp(PcapPacket packet) {
		packet.getHeader(icmp);

		byte[] buff = new byte[packet.getTotalSize()];
		packet.transferStateAndDataTo(buff);
		JBuffer jb = new JBuffer(buff);
		String content = jb.toHexdump();
		System.out.println("--content---" + content); // 打印
	}

	private void handleTcp(PcapPacket packet) {
		packet.getHeader(tcp);
		String srcPort = String.valueOf(tcp.source());
		String dstPort = String.valueOf(tcp.destination());
		System.out.println("********srcPort*********" + srcPort);
		System.out.println("********dstPort*********" + dstPort);

		byte[] buff = new byte[packet.getTotalSize()];
		packet.transferStateAndDataTo(buff);
	}

	private void handleUdp(PcapPacket packet) {
		packet.getHeader(udp);

		byte[] buff = new byte[packet.getTotalSize()];
		packet.transferStateAndDataTo(buff);
	}

	private void handleHttp(PcapPacket packet) {
		packet.getHeader(http);
		final String content_length = http.fieldValue(Response.Content_Length);
		packet.getHeader(tcp);
		Integer int_tcp_source = new Integer(tcp.source());
		if (int_tcp_source != 80 && content_length == null) {
			String host = http.fieldValue(Http.Request.Host);
			String url = host + http.fieldValue(Http.Request.RequestUrl);
			String referer = http.fieldValue(Http.Request.Referer);
			String useragent = http.fieldValue(Http.Request.User_Agent);

			System.out.println("host::::::" + host);
			System.out.println("url::::::" + url);
			System.out.println("refer::::::" + referer);
			System.out.println("useragent::::::" + useragent);
		}
	}

}