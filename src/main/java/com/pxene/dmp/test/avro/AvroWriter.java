package com.pxene.dmp.test.avro;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.Schema.Parser;
import org.apache.avro.specific.SpecificDatumWriter;

public class AvroWriter {

	public static void main(String[] args) throws IOException {
		User u1 = new User();
		u1.setName("ningyu");
		u1.setFavoriteNumber(128);
		u1.setFavoriteColor("black");
		User u2 = new User();
		u2.setName("chenjinghui");
		u2.setFavoriteNumber(256);
		u2.setFavoriteColor("red");
		
		DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
		DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
		
		dataFileWriter.create(User.getClassSchema(), new File("users.avro"));
		dataFileWriter.append(u1);
		dataFileWriter.append(u2);
		dataFileWriter.close();
	}
}
