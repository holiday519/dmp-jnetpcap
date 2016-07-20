package com.pxene.dmp.common;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

public class AvroWriter {
	
	public static <T> void write(T bean) {
		DatumWriter<T> mDw = new SpecificDatumWriter<T>();
	}

//	public void writeToAvro(PcapPacket packet,
//			FSDataOutputStream outputStream) throws Exception {
//
//		Schema schema = new Schema.Parser().parse(new File("user.avsc"));
//
//		DatumWriter<DpiData> userDatumWriter = new SpecificDatumWriter<DpiData>(
//				DpiData.class);
//		DataFileWriter<DpiData> writer = new DataFileWriter<DpiData>(
//				userDatumWriter);
//
//		writer.setCodec(CodecFactory.snappyCodec());
//		writer.create(schema, outputStream);
//
//		DpiData dpiData = DpiData.getHttpPackage(packet);
//		
//		writer.append(dpiData);
//		writer.close();
//
//	}

}
