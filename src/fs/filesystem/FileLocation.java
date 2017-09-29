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
//file location
public class FileLocation {
	public static void main(String[] args) {
		String uri = "hdfs://master:9000/input/wordtest";
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(URI.create(uri), conf);
			Path fpath = new Path(uri);
			FileStatus filestatus = fs.getFileStatus(fpath);
			BlockLocation[] blkLocations = fs.getFileBlockLocations(filestatus, 0, filestatus.getLen());
			int blockLen = blkLocations.length;
			for (int i = 0; i < blockLen; i++) {
				String[] hosts = blkLocations[i].getHosts();
				System.out.println("block_" + i + "_location:" + hosts[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}