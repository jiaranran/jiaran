package org.jeecg.modules.stkpredict.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.input.service.impl.ScmNretailIpwResultServiceImpl;
import org.jeecg.modules.result.service.IScmNretailResultService;
import org.jeecg.modules.stkjob.entity.ScmNretailJobStk;
import org.jeecg.modules.stkjob.service.IScmNretailJobStkService;
import org.jeecg.modules.stkscm.service.ScmNretailIpdService;
import org.jeecg.modules.stkpredict.entity.ScmNretailIpd;
import org.jeecg.modules.stkpredict.entity.ScmNretailStk;
import org.jeecg.modules.stkpredict.service.IScmNretailStkService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.stkresult.service.impl.ScmNretailIpdStkServiceImpl;
import org.jeecg.modules.time.entity.ScmNretailTime;
import org.jeecg.modules.time.service.IScmNretailTimeService;
import org.jeecg.modules.util.CsvUtil;
import org.jeecg.modules.util.FileUtil;
import org.jeecg.modules.util.ReadExcel;
import org.jeecg.modules.util.ResouceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.Api;


 /**
 * @Description: 原始数据
 * @Author: jeecg-boot
 * @Date:   2020-07-06
 * @Version: V1.0
 */
@Slf4j
@Api(tags="原始数据")
@RestController
@RequestMapping("/stkpredict/scmNretailStk")
public class ScmNretailStkController {
	@Autowired
	private IScmNretailStkService scmNretailStkService;
	 @Autowired
	 IScmNretailTimeService scmNretailTimeService;
	 @Autowired
	 private ScmNretailIpdService scmNretailIpdService;
	 @Autowired
	 private IScmNretailResultService scmNretailResultService;
	 @Autowired
	 ScmNretailIpdStkServiceImpl scmNretailIpdStkService;
	 @Autowired
	 private ScmNretailIpwResultServiceImpl scmNretailIpwResultService;
	 @Autowired
	 private IScmNretailJobStkService scmNretailJobStkService;
	 @Autowired
	 ResourceLoader resourceLoader;

	 public List<ScmNretailStk> readExcel(String sourceFilePath,String fileName, String uuid, String database){
		 if(fileName==null||fileName==""){
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			 fileName = "scm_nretail_"+sdf.format(new Date())+".xlsx";
		 }
		 File file = new File(sourceFilePath+fileName);

		 List<ScmNretailStk> listscmNretails = new ReadExcel().getExcelInfo(file);
		 ScmNretailJobStk job = new ScmNretailJobStk();
		 if(listscmNretails==null){
			 job.setState("fail");
			 job.setUuid(uuid);
			 job.setJobinfo("文件格式错误，请重新上传!");
			 job.setEndtime(new Date());
			 log.error("文件格式错误，请重新上传!");
			 scmNretailJobStkService.update(job,database);
		 }else {
			 job.setUuid(uuid);
			 job.setJobinfo("文件格式校验通过!");
			 log.info("文件格式校验通过!");
			 scmNretailJobStkService.update(job,database);
		 }
		 return listscmNretails;
	 }
	 public List<ScmNretailTime> readExcel2(String sourceFilePath, String fileName, String uuid, String database){
		 if(fileName==null||fileName==""){
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			 fileName = "scm_time"+sdf.format(new Date())+".xlsx";
		 }
		 File file = new File(sourceFilePath+fileName);
		 List<ScmNretailTime> scmTimes = new ReadExcel().getExcelInfo2(file);
		 ScmNretailJobStk job = new ScmNretailJobStk();
		 if(scmTimes==null){
			 job.setState("fail");
			 job.setUuid(uuid);
			 job.setJobinfo("提前期文件格式错误，请重新上传!");
			 job.setEndtime(new Date());
			 log.error("提前期文件格式错误，请重新上传!");
			 scmNretailJobStkService.update(job,database);
		 }else {
			 job.setUuid(uuid);
			 job.setJobinfo("提前期文件格式校验通过!");
			 log.info("提前期文件格式校验通过!");
			 scmNretailJobStkService.update(job,database);
		 }
		 return scmTimes;
	 }
	 public Boolean saveExcel2(String uuid,List<ScmNretailTime> scmTimes, String database){
		 ScmNretailJobStk job = new ScmNretailJobStk();
		 try {
			 scmNretailTimeService.truncate(database);
			 scmNretailTimeService.save(scmTimes, database);
			 job.setUuid(uuid);
			 job.setJobinfo("提前期数据入库成功！数据行数:" + scmTimes.size());
			 scmNretailJobStkService.update(job,database);
			 log.info("提前期数据入库成功！数据行数:" + scmTimes.size());
			 return true;
		 }catch (Exception e){
			 e.printStackTrace();
			 job.setUuid(uuid);
			 job.setState("fail");
			 job.setJobinfo("提前期数据入库失败");
			 job.setEndtime(new Date());
			 scmNretailJobStkService.update(job,database);
			 return false;
		 }
	 }
	 public Boolean saveExcel(String uuid,List<ScmNretailStk> listscmNretails, String database){
		 ScmNretailJobStk job = new ScmNretailJobStk();
		 try {
			 scmNretailStkService.truncate(database);
			 scmNretailStkService.save(listscmNretails, database);
			 job.setUuid(uuid);
			 job.setJobinfo("原始数据入库成功！数据行数:" + listscmNretails.size());
			 scmNretailJobStkService.update(job,database);
			 log.info("原始数据入库成功！数据行数:" + listscmNretails.size());
			 return true;
		 }catch (Exception e){
			 e.printStackTrace();
			 job.setUuid(uuid);
			 job.setState("fail");
			 job.setJobinfo("原始数据入库失败");
			 job.setEndtime(new Date());
			 scmNretailJobStkService.update(job,database);
			 return false;
		 }
	 }
	 public List<ScmNretailIpd> countByGpw(String uuid, String database, String fileName){
		 ScmNretailJobStk job = new ScmNretailJobStk();
		 try {
			 List<ScmNretailIpd> scmNretailIpds = scmNretailStkService.countByGpw(database);
			 log.info("销量数据汇总成功！数据行数:" + scmNretailIpds.size());
			 scmNretailIpds = scmNretailStkService.dsList2(scmNretailIpds,database, fileName);
			 log.info("缺失日期补全成功！数据行数:" + scmNretailIpds.size());
			 job.setUuid(uuid);
			 job.setJobinfo("销量数据汇总补全成功！数据行数:" + scmNretailIpds.size());
			 scmNretailJobStkService.update(job,database);
			 return scmNretailIpds;
		 }catch (Exception e){
			 e.printStackTrace();
			 job.setUuid(uuid);
			 job.setState("fail");
			 job.setJobinfo("销量数据汇总失败");
			 job.setEndtime(new Date());
			 scmNretailJobStkService.update(job,database);
			 return null;
		 }

	 }
	 public Boolean saveGpwData(String uuid, List<ScmNretailIpd> scmNretailIpds, String database){
		 boolean save = scmNretailIpdService.save(uuid, scmNretailIpds, database);
		 if(save){
			 log.info("汇总数据入库成功！数据行数:" + scmNretailIpds.size());
			 ScmNretailJobStk job = new ScmNretailJobStk();
			 job.setUuid(uuid);
			 job.setJobinfo("汇总数据入库成功！数据行数:" + scmNretailIpds.size());
			 scmNretailJobStkService.update(job,database);
		 }
		 return save;
	 }
	 
	 public void predict2(Double dependRate,String cntreadFilePath,String timereadFilePath,ArrayList<String> readfileNames,String outFilePath,String logFilePath,String pythonCommand,String beforeCommand,String uuid,String database){
		 scmNretailIpwResultService.truncate();
		 ArrayList<Future<Boolean>> list = new ArrayList<>();
		 ScmNretailIpdStkServiceImpl.fail_num=0;
		 ScmNretailIpdStkServiceImpl.success_num=0;
		 ScmNretailIpdStkServiceImpl.presuccess_num=0;
		 ScmNretailIpdStkServiceImpl.prefail_num=0;
		 for (String readfileName:readfileNames) {
			 String replace = readfileName.replace("ipw", "time");
			 int index = replace.lastIndexOf(".");
			 String tprov = replace.substring(index - 6, index-2);
			 replace=replace.substring(0,index-2)+".csv";
			 File file = new File(timereadFilePath+tprov+File.separator+replace);
			 if(!file.exists()){
				 log.error(timereadFilePath+tprov+File.separator+replace+"提前期文件不存在");
				continue;
			 }
//			Future<Boolean> future = pc.predict(latch,readFilePath, readfileName, outFilePath, pythonCommand, beforeCommand,startnum);
			 Future<Boolean> future = scmNretailIpdStkService.predict(dependRate,uuid,cntreadFilePath, timereadFilePath,readfileName, outFilePath, logFilePath, pythonCommand, beforeCommand);
			 list.add(future);
		 }
		 ScmNretailJobStk job = new ScmNretailJobStk();
		 job.setUuid(uuid);
		 scmNretailJobStkService.updateState(job,database,list);
	 }
	 @Async("asyncServiceExecutor")
	 public void updateState(CountDownLatch latch, String uuid, String database , ArrayList<Future<Boolean>> list) {
		 Boolean b=true;
		 ScmNretailJobStk job = new ScmNretailJobStk();
		 job.setUuid(uuid);
		 try {
			 latch.await();
			 for (Future<Boolean> future:list) {
				 if (!future.get()){
					 b=false;
				 }
			 }
			 if(b){
				 job.setState("success");
				 scmNretailJobStkService.update(job,database);
				 log.info("预测任务成功");
			 }else {
				 log.info("预测任务失败");
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
			 log.info("预测任务失败");
		 }

	 }
	 
	 @RequestMapping(value = "/jobList", method = RequestMethod.GET)
	 public Result<?> jobList(@RequestParam(name = "database", defaultValue = "mysql") String database, ScmNretailJobStk job) {
		 List<Map<String, Object>> list = scmNretailJobStkService.jobList(job,database);
		 return Result.ok(list);
	 }

	 /**
	  * 读取路径下文件
	  *
	  * @return
	  */
	 @RequestMapping(value = "/startTask", method = RequestMethod.POST)
	 @Async("asyncServiceExecutor")
	 public void startTask(Double dependRate,String fileName,String timeFileName,String uuid) {
		 Map<String,String> resource =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\dataPath.properties");
		 String sourceFilePath = resource.get("stksourceFilePath");
		 String cntReadFilePath = resource.get("stkcntFilePath");
		 String timeReadFilePath = resource.get("stktimeFilePath");
		 String outFilePath = resource.get("stkoutFilePath");
		 String logFilepath = resource.get("stklogFilePath");
		 String pythonCommand = resource.get("stkpythonCommand");
		 String beforeCommand = resource.get("beforeCommand");
		 Map<String,String> resource2 =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\jeecg_database.properties");
		 String database = resource2.get("database");
		 try{
			 //	数据内容校验
			 List<ScmNretailStk> listscmNretails = readExcel(sourceFilePath, fileName, uuid, database);
			 List<ScmNretailTime> scmTimes = readExcel2(sourceFilePath, timeFileName, uuid, database);
			 if(listscmNretails==null||scmTimes==null){return ;}
			 //	原始数据入库
			 Boolean b1 = saveExcel(uuid, listscmNretails, database);
			 Boolean b2 = saveExcel2(uuid, scmTimes, database);
			 if(!b1||!b2){return ;}
			 //	拆分汇总按天
			 List<ScmNretailIpd> scmNretailIpds = countByGpw(uuid,database, fileName);
			 if(scmNretailIpds==null){return ;}
			 //	汇总数据入库
			 Boolean aBoolean = saveGpwData(uuid, scmNretailIpds, database);
			 if(!aBoolean){return ;}
			 //	导出csv
			 ArrayList<String> readfileNames = exportCsv(uuid,scmNretailIpds,cntReadFilePath,database);
			 ArrayList<String> timefileNames = exportTimeCsv(uuid,timeReadFilePath,database);
			 if(readfileNames==null||timefileNames==null){return ;}
			 //	调用python脚本预测
			 predict2( dependRate,cntReadFilePath, timeReadFilePath,readfileNames, outFilePath, logFilepath,pythonCommand, beforeCommand,  uuid, database);

		 }catch (Exception e) {
			 ScmNretailJobStk job = new ScmNretailJobStk();
			 job.setUuid(uuid);
			 job.setState("fail");
			 job.setEndtime(new Date());
			 scmNretailJobStkService.update(job,database);
			 log.error(e.getMessage(),e);
		 }
	 }

	 //	 导出csv输入模型文件
	 public ArrayList<String> exportTimeCsv(String uuid,String writePath,String database){
		 ArrayList<String> readfileNames = null;
		 try {
			 List<ScmNretailTime> scmTimes= scmNretailTimeService.getAll(database);
//			 按照一个物料、一个省份为一个文件拆分汇总数据
//			 List<ScmNretailpiVo> scmNretailpiVos = scmNretailIpdService.countByGpw(database);
			 readfileNames= CsvUtil.exportTimeCsv(scmTimes,writePath);
			 log.info("导出提前期CSV文件成功");
			 ScmNretailJobStk job = new ScmNretailJobStk();
			 job.setUuid(uuid);
			 job.setJobinfo("导出提前期CSV文件成功");
			 scmNretailJobStkService.update(job,database);
		 } catch (IOException e) {
			 ScmNretailJobStk job = new ScmNretailJobStk();
			 job.setUuid(uuid);
			 job.setState("fail");
			 job.setJobinfo("导出提前期CSV文件失败");
			 job.setEndtime(new Date());
			 scmNretailJobStkService.update(job,database);
			 log.error("导出提前期CSV文件失败");
			 e.printStackTrace();
			 return null;
		 }
		 return readfileNames;
	 }
	 public ArrayList<String> exportCsv(String uuid,List<ScmNretailIpd> list,String writePath,String database){
		 ArrayList<String> readfileNames = null;
		 try {
			 List<ScmNretailIpd> scmNretailIpds= scmNretailIpdService.getAll(database);
//			 按照一个物料、一个省份为一个文件拆分汇总数据
//			 List<ScmNretailpiVo> scmNretailpiVos = scmNretailIpdService.countByGpw(database);
			 readfileNames=CsvUtil.exportCsv3(scmNretailIpds,writePath);
			 log.info("导出CSV文件成功");
			 ScmNretailJobStk job = new ScmNretailJobStk();
			 job.setUuid(uuid);
			 job.setJobinfo("导出CSV文件成功");
			 scmNretailJobStkService.update(job,database);
		 } catch (IOException e) {
			 ScmNretailJobStk job = new ScmNretailJobStk();
			 job.setUuid(uuid);
			 job.setState("fail");
			 job.setJobinfo("导出CSV文件失败");
			 job.setEndtime(new Date());
			 scmNretailJobStkService.update(job,database);
			 log.error("导出CSV文件失败");
			 e.printStackTrace();
			 return null;
		 }
		 return readfileNames;
	 }
	 @RequestMapping(value = "/readResFile", method = RequestMethod.GET)
	 @Async("asyncServiceExecutor")
	 public Future<Boolean> readResFile(CountDownLatch latch,String uuid, String path, String database, String readfileName, Future<Boolean> future) {
		 return null;
	 }

}
