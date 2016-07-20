package com.pxene.dmp.common;

import java.io.File;

import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.jnetpcap.packet.PcapPacket;

import com.pxene.dmp.bean.DpiData;

public class AvroWriter {

	public void writeToAvro(PcapPacket packet,
			FSDataOutputStream outputStream) throws Exception {

		Schema schema = new Schema.Parser().parse(new File("user.avsc"));

		DatumWriter<DpiData> userDatumWriter = new SpecificDatumWriter<DpiData>(
				DpiData.class);
		DataFileWriter<DpiData> writer = new DataFileWriter<DpiData>(
				userDatumWriter);

		writer.setCodec(CodecFactory.snappyCodec());
		writer.create(schema, outputStream);

		DpiData dpiData = DpiData.getHttpPackage(packet);
		
		writer.append(dpiData);
		writer.close();

	}

}
