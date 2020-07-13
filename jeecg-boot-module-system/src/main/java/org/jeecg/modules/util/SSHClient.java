package org.jeecg.modules.util;

import ch.ethz.ssh2.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class SSHClient {

	// 
	private Connection conn;
	
	// IP
	private String ip = "10.2.40.40";
	
	// 用户名
	private String username = "cmcc";
	
	// 密码
	private String password = "CMCC@22F";
	private static final int TIME_OUT = 0;// 表示不超时

	/**
	 * 使用默认配置
	 */
	public SSHClient() {
		String s = "[IP: " + ip + "]-[username: " + username + "]-[password: ******]。";
		System.out.println("使用默认配置进行连接通讯，参数为：" + s
				+ "\r\n连接默认不超时。");
	}
	
	/**
	 * 
	 * 不使用默认配置
	 * 
	 * @param ip       远程ip
	 * @param username 远程机器用户名
	 * @param password 远程机器密码
	 */
	public SSHClient(String ip, String username, String password) {
		this.ip = ip;
		this.username = username;
		this.password = password;
		String s = "[IP: " + ip + "]-[username: " + username + "]-[password: ******]。";
		System.out.println("使用传参方式配置进行连接通讯，参数为：" + s
				+ "\r\n连接默认不超时。");
	}

	/**
	 * 登录
	 *
	 * @return conn
	 * @throws IOException
	 */
	private boolean login() throws IOException {
		conn = new Connection(ip);
		conn.connect();
		return conn.authenticateWithPassword(username, password);
	}

	/**
	 * 执行脚本
	 *
	 * @param shell
	 * @return
	 * @throws Exception
	 */
	public int exec(String shell) throws Exception {
		int ret = -1;
		Session session = null;
		try {
			if (login()) {
				session = conn.openSession();
				session.execCommand(shell);
				// 获取返回输出
				InputStream stdout = new StreamGobbler(session.getStdout());
				// 返回错误输出
				InputStream stderr = new StreamGobbler(session.getStderr());
				
//				Thread error = new Thread(new InputStreamRunnable(new StreamGobbler(session.getStderr()), "ErrorStream"));
//				Thread outPut = new Thread(new InputStreamRunnable(new StreamGobbler(session.getStdout()), "OutPutStream"));
				BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
				BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
				
				while (true) {
					String line = stdoutReader.readLine();
					if (line == null)
						break;
					System.out.println("out - " + line);
				}

				while (true) {
					String line = stderrReader.readLine();
					if (line == null)
						break;
					System.err.println("err - " + line);
				}
				stderrReader.close();
				stdoutReader.close();
				stderr.close();
				session.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
				ret = session.getExitStatus();
			} else {
				throw new Exception("登录远程机器失败" + ip); // 自定义异常类
			}
		} finally {
			if (session != null) {
				session.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return ret;
	}
	
	/**
	 * 上传文件
	 * @param uploadFile 上传的文件
	 * @param writePath 写入文件夹的路径
	 * @return
	 */
	public int uploadFile(File uploadFile, String writePath) {
		try {
			login();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SCPClient scpClient = new SCPClient(conn);
		try {
			scpClient.put(fileToByte(uploadFile), uploadFile.getName(), writePath);
			System.out.println("上传文件成功");
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 18;
		}
	}
	
	/**
	 * 下载文件
	 * 
	 * @param filePath 本地创建的file路径
	 * @param readPath 读取文件的路径
	 * @return file路径
	 * @throws Exception
	 */
	public String downloadFile(String filePath, String readPath) throws Exception {
		try {
			login();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SFTPv3Client client = new SFTPv3Client(conn);
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		SFTPv3FileHandle handle = client.openFileRO(readPath);
		byte[] readBuffer = new byte[100];
		long offset = 0;
		int bytesRead = 0;

		while ((bytesRead = client.read(handle, offset, readBuffer, 0,
				readBuffer.length)) >= 0) {
			outBytes.write(readBuffer, 0, bytesRead);
			offset += bytesRead;
		}
		client.closeFile(handle);
		
		byte[] bs = outBytes.toByteArray();
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
		outputStream.write(bs);
		
		outputStream.close();
		outBytes.close();
		client.close();
		return filePath;
	}
	
	/**
	 * 获取文件夹下的文件名列表
	 * 
	 * @param directory 文件夹全名称
	 * @return 文件名列表
	 * @throws Exception
	 */
	public List<String> getFileList(String directory) throws Exception {
		try {
			login();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SFTPv3Client client = new SFTPv3Client(conn);
		
		List<String> fns = new ArrayList<String>();
		Vector<?> files = (Vector<?>) client.ls(directory);

		for (Iterator<?> elements = files.iterator(); elements.hasNext();) {
			SFTPv3DirectoryEntry file = (SFTPv3DirectoryEntry) elements.next();
			String fileName = file.filename.trim();
			// 文件列表
			fns.add(fileName);
		}
		
		return fns;
	}
	
	public byte[] fileToByte(File tradeFile){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }

	public static void main(String[] args) {
		try {
			SSHClient executor = new SSHClient();
			// 测试
			int exec = executor.exec("python /home/cmcc/anaconda3/code/kpi_warning/main_fun.py");
			System.out.println("" + exec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}