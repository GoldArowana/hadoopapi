package fs.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URI;
import java.net.MalformedURLException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
//file is exist ?
public class CheckFileIsExist {
	public static void main(String[] args) {
		String uri = "hdfs://master:9000/text123";
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(URI.create(uri), conf);
			Path path = new Path(uri);
			boolean isExists = fs.exists(path);
			System.out.println(isExists);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}