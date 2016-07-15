package com.pxene.dmp.test;
import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler;  
   
public class MyPcapPacketHandler<T> implements PcapPacketHandler<T>  {//抓到包后送去检测  
       
    @Override  
    public void nextPacket(PcapPacket packet, T obj) {  
        PacketMatch packetMatch = PacketMatch.getInstance();  
        packetMatch.handlePacket(packet);  
    }  
}  