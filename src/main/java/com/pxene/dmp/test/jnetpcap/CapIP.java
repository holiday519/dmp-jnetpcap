package com.pxene.dmp.test.jnetpcap;

import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class CapIP {

	public static void main(String[] args) {
		System.out.println("####################begin#######################");

		StringBuilder errbuf = new StringBuilder();
		List<PcapIf> ifs = new ArrayList<PcapIf>(); // Will hold list of devices
		int statusCode = Pcap.findAllDevs(ifs, errbuf);
		if (statusCode != Pcap.OK) {
			System.out.println("Error occurred: " + errbuf.toString());
			return;
		} else {
			for (int i = 0; i < ifs.size(); ++i) {
				System.out.println(ifs.get(i).getName() + "===="
						+ ifs.get(i).getDescription());// 输出所有网络接口的描述
			}
		}

		System.out.println("####################end#######################");
	}
}