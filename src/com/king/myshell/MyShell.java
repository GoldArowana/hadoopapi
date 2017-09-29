package com.king.myshell;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;

import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class MyShell {
	private static final String HOME_URL = new String("hdfs://master:9000/");
	private static Scanner scanner = null;
	private static Configuration conf = null;
	private static FileSystem fs = null;
	static {
		//将配置和连接对象存放到静态代码块，随类生存。
		//静态变量区域的成员一旦创建，直到程序退出才会被回收。
		//如果是多线程则建议改成构造块+连接池
		scanner = new Scanner(System.in);
		conf = new Configuration();
		try {
			fs = FileSystem.get(URI.create(HOME_URL), conf);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.print("FileSystem在静态代码块加载失败");
		}
	}

	public static void main(String[] args) {
		StringBuffer nowURL = new StringBuffer("hdfs://master:9000/");
		printSystemHead("金龙");
		System.out.print("连接成功!!!\n@shell:【" + nowURL + "】>>>");
		while (scanner.hasNext()) {
			String command = scanner.nextLine();
			String[] commandSplit = command.split(" ");
			switchCommand(nowURL, commandSplit);
			System.out.print("\n@shell:【" + nowURL + "】>>>");
		}
	}

	private static void printSystemHead(String name) {
		System.out.println("----------------------------------------");
		System.out.println("|*****欢迎来到【" + name + "】版hdfs-shell系统*******|");
		System.out.println("----------------------------------------");
		System.out.println("正在连接hdfs...请稍等");
	}

	private static void switchCommand(StringBuffer nowURL, String[] commandSplit) {
		try {
			switch (commandSplit[0]) {
			case "exists":   // @shell:【hdfs://master:9000/】>>> exists ttt          #用来判断hdfs系统中是否存在ttt文件(或文件夹)
				System.out.println("-->查询结果：" + (isExist(fs, nowURL, commandSplit) ? "[[存在]]" : "[[不存在]]"));
				break;
			case "cd":   // @shell:【hdfs://master:9000/】>>> cd input          #进入到input文件夹里(相对于当前目录)
						  // @shell:【hdfs://master:9000/】>>> cd input          #返回上一层目录(相对于当前目录)
				cd(fs, HOME_URL, nowURL, commandSplit);
				break;
			case "ls":  // @shell:【hdfs://master:9000/】>>> ls          #查看当前文件夹下所有文件(或文件夹/链接)
				ls(fs, nowURL);
				break;
			case "touch":  // @shell:【hdfs://master:9000/】>>> touch a.txt          #在当前目录创建a.txt文件
				touch(fs, nowURL, commandSplit);
				break;
			case "rm":    // @shell:【hdfs://master:9000/】>>> rm input          #删除当前目录下的文件(或文件夹)
				rm(fs, nowURL, commandSplit);
				break;
			case "mkdir":// @shell:【hdfs://master:9000/】>>> mkdir input          #在当前目录下创建input文件夹
				mkdir(fs, nowURL, commandSplit);
				break;
			case "cat": // @shell:【hdfs://master:9000/】>>>cat a.txt          #查看a.txt文件下的内容
				cat(fs, nowURL, commandSplit);
				break;
			case "cp": // @shell:【hdfs://master:9000/】>>>cp a.txt b.txt          #将本地的a.txt文件上传到hdfs系统中，并命名为b.txt
				cp(fs, nowURL, commandSplit);
				break;
			case "find": // @shell:【hdfs://master:9000/】>>>find ttt          #根据文件名ttt查找文件，列出所有结果
				find(fs, nowURL, commandSplit);
				break;
			case "grep":// @shell:【hdfs://master:9000/】>>>find stt          #列出包含内容‘stt’的所有文件
				grep(fs, nowURL, commandSplit);
				break;
			default:
				System.out.println("！！！没有该命令");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.print("输入命令有误");
		}
	}

	private static boolean isExist(FileSystem fs, StringBuffer homeURL, String[] commandSplit) throws IOException {
		String scanedFileName = commandSplit[1];
		System.out.println("--%正在执行任务：查询【" + scanedFileName + "】请稍等...");
		String fileToCheck = homeURL + "/" + scanedFileName;
		Path path = new Path(fileToCheck);
		boolean isExists = fs.exists(path);
		return isExists;
	}

	private static void cd(FileSystem fs, String homeURL, StringBuffer nowURL, String[] commandSplit)
			throws IOException {
		System.out.println("--%正在执行任务：进入目录【" + commandSplit[1] + "】请稍等...");
		if (commandSplit[1].startsWith("/")) {
			nowURL = new StringBuffer(homeURL);
		} else if (commandSplit[1].equals("..")) {
			if (!nowURL.toString().endsWith("hdfs://master:9000/")) {
				nowURL.delete(nowURL.lastIndexOf("/"), nowURL.length());
				nowURL.delete(nowURL.lastIndexOf("/"), nowURL.length());
				nowURL.append("/");
			} else {
				return;
			}
		} else {
			nowURL.append(commandSplit[1]);
			nowURL.append("/");
		}
		Path path = new Path(nowURL.toString());
		boolean result = fs.isDirectory(path);
		if (!result) {
			System.out.println("-->【进入失败。。没有该文件夹】");
			nowURL.delete(nowURL.length() - commandSplit[1].length() - 2, nowURL.length() - 1);
		}
	}

	private static void ls(FileSystem fs, StringBuffer nowURL) throws FileNotFoundException, IllegalArgumentException, IOException {
		System.out.println("--%正在执行任务：查询目录【" + nowURL + "】请稍等...");
		FileStatus[] stats = null;
		stats = fs.listStatus(new Path(nowURL.toString()));
		for (int i = 0; i < stats.length; ++i) {
			System.out.println("-->"+getType(stats[i])+"\t" + stats[i].getPath().toString());
		}
	}

	private static void cp(FileSystem fs, StringBuffer nowURL, String[] commandSplit) {
		String home = "/home/king/";
		String source = home + commandSplit[1];
		System.out.println(source);
		String destination = nowURL + commandSplit[2];
		System.out.println("--%正在执行任务：复制文件【" + destination + "】请稍等...");
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(source));
			OutputStream out = fs.create(new Path(destination));
			IOUtils.copyBytes(in, out, 4096, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void cat(FileSystem fs, StringBuffer nowURL, String[] commandSplit) {
		System.out.println("--%正在执行任务：查看文件内容【" + commandSplit[1] + "】请稍等...");
		String seekFile = new String(nowURL + commandSplit[1]);
		InputStream in = null;
		try {
			in = fs.open(new Path(seekFile));
			IOUtils.copyBytes(in, System.out, 4096, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(in);
		}
	}

	private static void mkdir(FileSystem fs, StringBuffer nowURL, String[] commandSplit) throws IOException {
		System.out.println("--%正在执行任务：创建文件夹【" + commandSplit[1] + "】请稍等...");
		String createPath = nowURL + commandSplit[1];
		Path path = new Path(createPath);
		boolean isSuccess = fs.mkdirs(path);
		System.out.print("-->文件夹【" + commandSplit[1] + "】创建" + (isSuccess ? "成功" : "失败"));
	}

	private static void rm(FileSystem fs, StringBuffer nowURL, String[] commandSplit) throws IOException {
		System.out.println("--%正在执行任务：删除文件(夹)【" + commandSplit[1] + "】请稍等...");
		String deleteFile = nowURL + commandSplit[1];
		Path path = new Path(deleteFile);
		boolean isDeleted = fs.delete(path, true);
		System.out.println("-->文件【" + deleteFile + "】删除" + (isDeleted ? "成功" : "失败"));
	}

	private static void touch(FileSystem fs, StringBuffer nowURL, String[] commandSplit)
			throws IllegalArgumentException, IOException {
		System.out.println("--%正在执行任务：创建文件【" + commandSplit[1] + "】请稍等...");
		String newFile = nowURL + commandSplit[1];
		fs.create(new Path(newFile));
		System.out.println("-->创建文件【" + commandSplit[1] + "】" + (isExist(fs, nowURL, commandSplit) ? "成功" : "失败"));
	}

	private static void find(FileSystem fs, StringBuffer nowURL, String[] commandSplit)
			throws FileNotFoundException, IllegalArgumentException, IOException {
		System.out.println("--%正在执行任务：按文件名查询文件【" + nowURL + "】请稍等...");
		FileStatus[] stats = fs.listStatus(new Path(nowURL.toString()));
		for (int i = 0; i < stats.length; ++i) {
			String name = stats[i].getPath().toString();
			String fileName = name.substring(name.lastIndexOf("/") + 1, name.length());
			if (fileName.indexOf(commandSplit[1]) != -1) {
				System.out.println("-->" + getType(stats[i]) + "\t" + stats[i].getPath().toString());
			}
		}
	}

	private static String getType(FileStatus stat) {
		if (stat.isFile()) {
			return "文件";
		} else if (stat.isDirectory()) {
			return "文件夹";
		} else { // stat.isSymlink()
			return "链接";
		}
	}

	private static void grep(FileSystem fs, StringBuffer nowURL, String[] commandSplit) throws IOException {
		System.out.println("--%正在执行任务：按文件内容[" + commandSplit[1] + "]查询文件【" + nowURL + "】请稍等...");
		FileStatus[] stats = fs.listStatus(new Path(nowURL.toString()));
		for (int i = 0; i < stats.length; ++i) {
			if (stats[i].isFile()) {
				String name = stats[i].getPath().toString();
				String fileName = name.substring(name.lastIndexOf("/") + 1, name.length());
				String dest = nowURL + fileName;
				BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(new Path(dest))));
				String line = null;
				do{
					line = br.readLine();// System.out.println(line);
					if (line != null && line.indexOf(commandSplit[1]) != -1) {
						System.out.println("-->文件\t" + stats[i].getPath().toString());
						break;
					}
				}while(line != null && line.indexOf(commandSplit[1]) != -1);
			}
		}
	}
}
