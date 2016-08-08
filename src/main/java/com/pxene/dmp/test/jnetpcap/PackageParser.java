package com.pxene.dmp.test.jnetpcap;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;

public class PackageParser {
	
	private static final String SEPARATOR = "|";
	
	public static void main(String[] args) {
		System.out.println("####################begin#######################");
		
		if (args.length < 1) {
			System.out.println("input network code");
			System.exit(1);
		}
		if (!args[0].matches("[0-9]+")) {
			System.out.println("network code must be 0-9");
			System.exit(1);
		}
		
		int code = Integer.parseInt(args[0]);
		List<PcapIf> devs = new ArrayList<PcapIf>();
		StringBuilder errsb = new StringBuilder();
		int r = Pcap.findAllDevs(devs, errsb);
		if (r == Pcap.NOT_OK || devs.isEmpty()) {
			JOptionPane.showMessageDialog(null, errsb.toString(), "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		PcapIf device = devs.get(code);
		// 长度65536
		int snaplen = Pcap.DEFAULT_SNAPLEN;
		// 混杂模式
		int flags = Pcap.MODE_PROMISCUOUS;
		int timeout = 10 * 1000;
		
		//捕获一个网络数据包
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errsb);
		if (pcap == null) {
			JOptionPane.showMessageDialog(null, errsb.toString(), "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// 根据条件过滤
		PcapBpfProgram program = new PcapBpfProgram();  
		pcap.compile(program, "port 80", 0, 0xFFFFFF00); 
		pcap.setFilter(program);
		
		pcap.loop(0, new PcapPacketHandler<Object>() {
			@Override
			public void nextPacket(PcapPacket packet, Object user) {
				// 源IP|目的IP|源端口|目的端口|method（get:1,post:2,http:3）|host|url|referer|userAgent|cookie|contentType|content|date
				// IP
				Ip4 ip = new Ip4();
				Tcp tcp = new Tcp();
				Http http = new Http();
				Payload payload = new Payload();
				if (packet.hasHeader(ip) && packet.hasHeader(tcp) && packet.hasHeader(http) && !http.isResponse()) {
					packet.getHeader(ip);
					String srcIP = FormatUtils.ip(ip.source()) == null ? "" : FormatUtils.ip(ip.source()).trim();
					String dstIP = FormatUtils.ip(ip.destination()) == null ? "" : FormatUtils.ip(ip.destination()).trim();
					
					packet.getHeader(tcp);
					String srcPort = String.valueOf(tcp.source()) == null ? "" : String.valueOf(tcp.source()).trim();
					String dstPort = String.valueOf(tcp.destination()) == null ? "" : String.valueOf(tcp.destination()).trim();
					
					packet.getHeader(http);
					String method = http.fieldValue(Http.Request.RequestMethod) == null ? "" : http.fieldValue(Http.Request.RequestMethod).trim();
					String host = http.fieldValue(Http.Request.Host) == null ? "" : http.fieldValue(Http.Request.Host).trim();
					String url = http.fieldValue(Http.Request.RequestUrl) == null ? "" : http.fieldValue(Http.Request.RequestUrl).trim();
					String referer = http.fieldValue(Http.Request.Referer) == null ? "" : http.fieldValue(Http.Request.Referer).trim();
					String userAgent = http.fieldValue(Http.Request.User_Agent) == null ? "" : http.fieldValue(Http.Request.User_Agent).trim();
					String cookie = http.fieldValue(Http.Request.Cookie) == null ? "" : http.fieldValue(Http.Request.Cookie).trim();
					String contentType = http.fieldValue(Http.Request.Content_Type) == null ? "" : http.fieldValue(Http.Request.Content_Type).trim();
					String date = http.fieldValue(Http.Request.Date) == null ? "" : http.fieldValue(Http.Request.Date).trim();
					
					String content = "";
					if (packet.hasHeader(payload)) {
						packet.getHeader(payload);
						content = payload.getUTF8String(0, payload.size());
					}
					
					System.out.println(srcIP + SEPARATOR + dstIP + SEPARATOR + srcPort + SEPARATOR + dstPort + SEPARATOR
							+ method + SEPARATOR + host + SEPARATOR + url + SEPARATOR + referer + SEPARATOR
							+ userAgent + SEPARATOR + cookie + SEPARATOR + contentType + SEPARATOR + content + SEPARATOR + date);
				}
			}
		}, "jnetpcap");
		pcap.close();
		
		System.out.println("####################end#######################");
	}
}
