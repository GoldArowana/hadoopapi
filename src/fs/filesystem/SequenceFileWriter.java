package fs.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URI;
import java.net.MalformedURLException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
// write file
public class SequenceFileWriter {
	private static final String[] text = { "hello hadoop", "bye hadoop", };

	public static void main(String[] args) {
		String uri = "hdfs://master:9000/input/testsequence";
		Configuration conf = new Configuration();
		SequenceFile.Writer writer = null;
		try {
			FileSystem fs = FileSystem.get(URI.create(uri), conf);
			Path path = new Path(uri);
			IntWritable key = new IntWritable();
			Text value = new Text();
			writer = SequenceFile.createWriter(fs, conf, path, key.getClass(), value.getClass());
			for (int i = 0; i < 100; i++) {
				key.set(100 - i);
				value.set(text[i % text.length]);
				writer.append(key, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(writer);
		}
	}
}
