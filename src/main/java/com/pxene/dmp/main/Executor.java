package com.pxene.dmp.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
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

import com.pxene.dmp.common.DateUtils;
import com.pxene.dmp.common.StringUtils;

public class Executor {
	
	private static final String SEPARATOR = "|";
	private static final Ip4 IP = new Ip4();
	private static final Tcp TCP = new Tcp();
	private static final Http HTTP = new Http();
	private static final Payload PAYLOAD = new Payload();
	private static final String TOPIC = "fixed_data";
	
	private static Producer<String, String> producer;
	
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
		// kafka producer
		Properties props = new Properties();
		props.put("bootstrap.servers", "dmp28:9092,dmp29:9092,dmp30:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		// 256MB
		props.put("buffer.memory", 268435456L);
		props.put("retries", 1);
		// 4MB
		props.put("max.request.size", 4194304);
		// 1MB
		props.put("receive.buffer.bytes", 1048576);
		// 4MB
		props.put("send.buffer.bytes", 4194304);
//		producer = new KafkaProducer<String, String>(props);
		
		int code = Integer.parseInt(args[0]);
		List<PcapIf> devs = new ArrayList<PcapIf>();
		StringBuilder errsb = new StringBuilder();
		int r = Pcap.findAllDevs(devs, errsb);
		if (r == Pcap.NOT_OK || devs.isEmpty()) {
			JOptionPane.showMessageDialog(null, errsb.toString(), "error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		PcapIf device = devs.get(code);
		// 长度65536
		int snaplen = Pcap.DEFAULT_SNAPLEN;
		// 混杂模式
		int flags = Pcap.MODE_PROMISCUOUS;
		int timeout = 5 * 1000;
		
		//捕获一个网络数据包
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errsb);
		if (pcap == null) {
			JOptionPane.showMessageDialog(null, errsb.toString(), "error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// 根据条件过滤
//		PcapBpfProgram program = new PcapBpfProgram();
//		pcap.compile(program, "port 80", 0, 0xFFFFFF00);
//		pcap.setFilter(program);
		
		pcap.loop(0, new PcapPacketHandler<Object>() {
			@Override
			public void nextPacket(PcapPacket packet, Object user) {
				// 源IP|目的IP|源端口|目的端口|method|host|url|referer|userAgent|cookie|contentType|content|date
				// IP
				if (packet.hasHeader(IP) && packet.hasHeader(TCP) && packet.hasHeader(HTTP) && !HTTP.isResponse()) {
					packet.getHeader(IP);
					String srcIP = StringUtils.null2Empty(FormatUtils.ip(IP.source()));
					String dstIP = StringUtils.null2Empty(FormatUtils.ip(IP.destination()));
					
					packet.getHeader(TCP);
					String srcPort = StringUtils.null2Empty(String.valueOf(TCP.source()));
					String dstPort = StringUtils.null2Empty(String.valueOf(TCP.destination()));
					
					packet.getHeader(HTTP);
					String method = StringUtils.null2Empty(HTTP.fieldValue(Http.Request.RequestMethod));
					String host = StringUtils.null2Empty(HTTP.fieldValue(Http.Request.Host));
					String url = StringUtils.null2Empty(HTTP.fieldValue(Http.Request.RequestUrl));
					String referer = StringUtils.null2Empty(HTTP.fieldValue(Http.Request.Referer));
					String userAgent = StringUtils.null2Empty(HTTP.fieldValue(Http.Request.User_Agent));
					String cookie = StringUtils.null2Empty(HTTP.fieldValue(Http.Request.Cookie));
					String contentType = StringUtils.null2Empty(HTTP.fieldValue(Http.Request.Content_Type));
					//String date = StringUtils.null2Empty(HTTP.fieldValue(Http.Request.Date));
					String content = "";
					if (packet.hasHeader(PAYLOAD)) {
						packet.getHeader(PAYLOAD);
						content = StringUtils.null2Empty(PAYLOAD.getUTF8String(0, PAYLOAD.size()));
						if (StringUtils.isGarbled(content)) {
							content = "";
						}
					}
					String date = DateUtils.getCurrentTime("YYYY-MM-dd HH:mm:ss.SSSSSS");
					
//					producer.send(new ProducerRecord<String, String>(TOPIC, srcIP + SEPARATOR + dstIP + SEPARATOR + srcPort + SEPARATOR + dstPort + SEPARATOR
//							+ method + SEPARATOR + host + SEPARATOR + url + SEPARATOR + referer + SEPARATOR
//							+ userAgent + SEPARATOR + cookie + SEPARATOR + contentType + SEPARATOR + content + SEPARATOR + date));
					
					System.out.println(srcIP + SEPARATOR + dstIP + SEPARATOR + srcPort + SEPARATOR + dstPort + SEPARATOR
							+ method + SEPARATOR + host + SEPARATOR + url + SEPARATOR + referer + SEPARATOR
							+ userAgent + SEPARATOR + cookie + SEPARATOR + contentType + SEPARATOR + content + SEPARATOR + date);
				}
			}
		}, "jnetpcap");
		
		producer.close();
		pcap.close();
		
		System.out.println("####################end#######################");
	}
	
}
