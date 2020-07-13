package org.jeecg.modules.util;


import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.stkjob.entity.ScmNretailJobStk;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * DO?
 * @author 10621
 *
 */
@Slf4j
@EnableAsync
@Component
public class PythonCommand {
	public Boolean predict(String readPath, String readfileName, String outFilepath,String logFilepath, String pythonCommand, String beforeCommant,int startnum) {
		boolean b=false;
		try {
			//接收正常结果流
			// outputStream = new ByteArrayOutputStream();
			//接收异常结果流
			//errorStream = new ByteArrayOutputStream();
			int index = readfileName.lastIndexOf(".");
			String prov = readfileName.substring(index - 6, index);
			File file = new File(outFilepath + prov);
			if(!file.exists()){
				file.mkdir();
			}
			File lfile = new File(logFilepath+ prov);
			if(!lfile.exists()){
				lfile.mkdir();
			}
				Runtime runtime = Runtime.getRuntime();
				String logfile = readfileName.substring(0, readfileName.lastIndexOf("."))+".log";
				String command = pythonCommand + " -i " + readPath +prov+File.separator+ readfileName + " -o " + outFilepath + prov+File.separator +readfileName +" "+startnum+ " |tee -a "+logFilepath+ prov+File.separator +logfile;
				log.info("exec_python_command : " + command);
				long start = System.currentTimeMillis();

//				Process process = runtime.exec(command, null, new File(beforeCommant));
				String readfilePath=readPath +prov+File.separator+ readfileName ;
				String outfilePath= outFilepath + prov+File.separator+readfileName;
				String logfilePath=logFilepath+ prov+File.separator+ logfile;
				String a =  "/root/cmdc/bin/a.sh "+readfilePath+" "+outfilePath+" "+logfilePath+" "+startnum;
//				String a="classpath:jeecg\\a.sh"+readfilePath+" "+outfilePath+" "+logfilePath+" "+startnum;
				System.out.println("a--------" + a);
				String[] comm = { "/bin/sh", "-c",a};
				Process process = runtime.exec(comm);
				StreamThread output = new StreamThread(process.getInputStream(), "output");
				StreamThread error = new StreamThread(process.getErrorStream(), "Error");
				output.start();
				error.start();
				/*// 正常日志
				InputStreamReader isr = new InputStreamReader(process.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while((line = br.readLine()) != null) {
					log.info("python_log_out : " + line);
				}

				// 异常日志
				InputStreamReader isrerr = new InputStreamReader(process.getErrorStream());
				BufferedReader brerr = new BufferedReader(isrerr);
				String lineerr = null;
				while((lineerr = brerr.readLine()) != null) {
					log.info("python_log_err : " + lineerr);
				}*/

				/*CommandLine commandline = new CommandLine(pythonCommand);
				commandline.addArgument(" -i "+readPath+readfileName,false);
				commandline.addArgument(" -o "+outFilepath+readfileName,false);
				commandline.addArgument(" |tee -a /root/cmdc/log/"+ readfileName,false);
				log.info(Arrays.toString(commandline.getArguments()));
				DefaultExecutor exec = new DefaultExecutor();
				exec.setExitValue(0);
				//设置一分钟超时
				ExecuteWatchdog watchdog = new ExecuteWatchdog(INFINITE_TIMEOUT);
				exec.setWatchdog(watchdog);

				PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream,errorStream);
				exec.setStreamHandler(streamHandler);
				int execute = exec.execute(commandline);*/

				int execute = process.waitFor();
//				int execute=0;
				log.info("python exit result: " + (execute == 0));
				if(execute!=0){
					log.info(readfileName+"预测脚本执行失败");
					return false;
				}
				long end = System.currentTimeMillis();
				log.info("脚本运行时间："+(end-start)/1000+"秒");
				//不同操作系统注意编码，否则结果乱码
				//String out = outputStream.toString("gbk");
				//String error = errorStream.toString("gbk");
				//log.info(out+error);
			ThreadPoolTaskExecutor asyncServiceExecutor = (ThreadPoolTaskExecutor) SpringContextUtils.getBean("asyncServiceExecutor");
			ThreadPoolExecutor threadPoolExecutor = asyncServiceExecutor.getThreadPoolExecutor();
			BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();

			System.out.println("队列线程数============"+queue.size());
			System.out.println("活动线程数============"+threadPoolExecutor.getActiveCount());

			b=true;
			log.info(readfileName+"预测脚本调度成功");
			return b;
		}catch (Exception e) {
			e.printStackTrace();
			log.info(readfileName+"预测脚本调度失败");
			return b;
		}
	}
	public Boolean predict(Double dependRate,String cntreadPath,String timereadPath ,String readfileName, String outFilepath,String logFilepath, String pythonCommand, String beforeCommant) {
		boolean b=false;
		try {
			int index = readfileName.lastIndexOf(".");
			String prov = readfileName.substring(index - 6, readfileName.lastIndexOf("."));
			File file = new File(outFilepath + prov);
			if(!file.exists()){
				file.mkdir();
			}
			String replace = readfileName.replace("ipw", "time");
			index = replace.lastIndexOf(".");
			String tprov = replace.substring(index - 6, index-2);
			replace=replace.substring(0,index-2)+".csv";

			Runtime runtime = Runtime.getRuntime();
			String logfile = readfileName.substring(0, readfileName.lastIndexOf("."))+".log";
			String command = pythonCommand + " -i " + cntreadPath +prov+File.separator+ readfileName + " -t " + timereadPath + tprov+File.separator +replace + " -o " + outFilepath + prov+File.separator +readfileName +" "+dependRate+ " |tee -a "+logFilepath+ prov+File.separator+logfile;
			log.info("exec_python_command : " + command);
			long start = System.currentTimeMillis();

//				Process process = runtime.exec(command, null, new File(beforeCommant));
			String cntfilePath=cntreadPath +prov+File.separator+ readfileName ;
			String timefilePath=timereadPath +tprov+File.separator+ replace ;
			String outfilePath= outFilepath + prov+File.separator+readfileName;
			String logfilePath=logFilepath+ prov+File.separator+ logfile;
			file = new File(logFilepath+ prov);
			if(!file.exists()){
				file.mkdir();
			}
			command =  "/root/cmdc/bin/b.sh "+cntfilePath+" "+timefilePath+" "+outfilePath+" "+dependRate+" "+logfilePath;
//				String a="classpath:jeecg\\a.sh"+readfilePath+" "+outfilePath+" "+logfilePath+" "+startnum;
			System.out.println("b--------" +command);
			String[] comm = { "/bin/sh", "-c",command};
			Process process = runtime.exec(comm);
			StreamThread output = new StreamThread(process.getInputStream(), "output");
			StreamThread error = new StreamThread(process.getErrorStream(), "Error");
			output.start();
			error.start();

			// 执行python first command
				/*Process beforePs = runtime.exec(beforeCommant);
				int beforeF = beforePs.waitFor();
				log.info("python_command_before_result: " + (beforeF == 0));

				Process process = runtime.exec(command, null, new File(beforeCommant));

				// 正常日志
				InputStreamReader isr = new InputStreamReader(process.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while((line = br.readLine()) != null) {
					log.info("python_log_out : " + line);
				}

				// 异常日志
				InputStreamReader isrerr = new InputStreamReader(process.getErrorStream());
				BufferedReader brerr = new BufferedReader(isrerr);
				String lineerr = null;
				while((lineerr = brerr.readLine()) != null) {
					log.info("python_log_err : " + lineerr);
				}*/

				/*CommandLine commandline = new CommandLine(pythonCommand);
				commandline.addArgument(" -i "+readPath+readfileName,false);
				commandline.addArgument(" -o "+outFilepath+readfileName,false);
				commandline.addArgument(" |tee -a /root/cmdc/log/"+ readfileName,false);
				log.info(Arrays.toString(commandline.getArguments()));
				DefaultExecutor exec = new DefaultExecutor();
				exec.setExitValue(0);
				//设置一分钟超时
				ExecuteWatchdog watchdog = new ExecuteWatchdog(INFINITE_TIMEOUT);
				exec.setWatchdog(watchdog);

				PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream,errorStream);
				exec.setStreamHandler(streamHandler);
				int execute = exec.execute(commandline);*/

				int execute = process.waitFor();
			log.info("python exit result: " + (execute == 0));
			if(execute!=0){
				log.info(readfileName+"安全库存脚本脚本执行失败");
				return false;
			}
			long end = System.currentTimeMillis();
			log.info(readfileName+"脚本运行时间："+(end-start)/1000+"秒");
			//不同操作系统注意编码，否则结果乱码
			//String out = outputStream.toString("gbk");
			//String error = errorStream.toString("gbk");
			//log.info(out+error);
			ThreadPoolTaskExecutor asyncServiceExecutor = (ThreadPoolTaskExecutor) SpringContextUtils.getBean("asyncServiceExecutor");
			ThreadPoolExecutor threadPoolExecutor = asyncServiceExecutor.getThreadPoolExecutor();
			BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
			System.out.println("队列线程数============"+queue.size());
			System.out.println("活动线程数============"+threadPoolExecutor.getActiveCount());
			b=true;
			log.info(readfileName+"安全库存脚本调度成功");
			return b;
		}catch (Exception e) {
			e.printStackTrace();
			log.info(readfileName+"安全库存脚本调度失败");
			return b;
		}
	}
	/**
	 * 指标预测-临时任务
	 *   FT_WRITE_PATH: python生成文件的路径
	 * @param readPath 文件读取路径
	 * @param readFileName 读取文件时的名称
	 */
	public void forecastTask(String readPath, String readFileName) {
//		python /home/cmcc/anaconda3/code/temporary_forecast/main_fun.py 12 1 $DATA/input/.csv  $DATA/predict/.csv   temporary_read_path
		String temporary_read_path="/home/cmcc/dgs/file/temporary/";
		final String pythonCommand = subPythonCommand("/temporary_forecast/main_fun.py", "12", "1", readPath, readFileName,temporary_read_path);

		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				SSHClient ssh = new SSHClient();
				try {
					int exit = ssh.exec(pythonCommand);
					log.info("指标预测-临时任务shh通信退出状态：" + exit);

				} catch (Exception e) {

					e.printStackTrace();
					throw new RuntimeException("ERROR: 指标预测-临时任务python执行时异常");
				}
			}
		};
		thread.start();
	}
	/**
	 * 拼接python完整命令
	 * @param pythonName python文件名称
	 * @param parameters python所需参数
	 * @return String: python完整命令
	 */
	public String subPythonCommand(String pythonName, String... parameters) {
		// 格式化python文件全路径
		pythonName = parse(pythonName);
		// 命名标准头部
		String cmd = pythonName + " ";
		// 参数
		for(String s : parameters) {
			cmd += s + " ";
		}
		cmd = cmd.substring(0, cmd.length() - 1);
		log.info("=== 本次拼接的python命令为: " + cmd + " ===");
		return cmd;
	}
	public static String parse(String path) {
		String python_command="python /home/cmcc/anaconda3/code";
		// 主动设置python编码命令开头加: PYTHONIOENCODING=utf-8
		return python_command + path;
	}

	public static void main(String[] args) {
		String beforeCommand="/root/cmdc/bin/python";
		String pythonCommand="/opt/anaconda3/bin/python /root/cmdc/bin/python/main_online_go.py";
		String readFilePath="/root/cmdc/data/input/";
		String outFilePath="/root/cmdc/data/predict/";
		String sourceFilePath="/root/cmdc/data/raw/";
		String logfilePath=" /root/cmdc/log/";
		System.out.println("/root/cmdc/bin/a.sh "+readFilePath+" "+outFilePath+" "+logfilePath);
	}
}
