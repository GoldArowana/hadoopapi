package filecopy;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
//4 copy file
public class FileCopyFromLocal {
	public static void main(String[] args) throws IOException {
		String source = "/home/king/wordtest";
		String destination = "hdfs://master:9000/input/text456123123";
		InputStream in = new BufferedInputStream(new FileInputStream(source));
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(destination), conf);
		OutputStream out = fs.create(new Path(destination));
		IOUtils.copyBytes(in, out, 4096, true);
	}
}