package org.jeecg.modules.predict.controller;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.input.service.impl.ScmNretailIpwResultServiceImpl;
import org.jeecg.modules.job.entity.ScmNretailJob;
import org.jeecg.modules.job.service.ScmNretailJobService;
import org.jeecg.modules.predict.entity.ScmNretail;
import org.jeecg.modules.predict.entity.ScmNretailIpw;
import org.jeecg.modules.predict.service.ScmNretailService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.predict.vo.ScmNretailDsVo;
import org.jeecg.modules.result.service.IScmNretailResultService;
import org.jeecg.modules.result.service.impl.ScmNretailResultServiceImpl;
import org.jeecg.modules.scm.service.ScmNretailIpwService;
import org.jeecg.modules.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import io.swagger.annotations.Api;

/**
 * @Description: 原始数据
 * @Author: jeecg-boot
 * @Date:   2020-03-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags="原始数据")
@RestController
@RequestMapping("/predict/scmNretail")
public class ScmNretailController {
	 @Autowired
	 private ScmNretailService scmNretailService;
	 @Autowired
	 private ScmNretailIpwService scmNretailIpwService;
	 @Autowired
	 private IScmNretailResultService scmNretailResultService;
	 @Autowired
	 private ScmNretailIpwResultServiceImpl scmNretailIpwResultService;
	 @Autowired
	 private ScmNretailJobService scmNretailJobService;
	 @Autowired
	 ResourceLoader resourceLoader;

  public void updateJob(String uuid,String database,long time){
//	  scmNretailJobService.update(uuid,"fail",database);
	  scmNretailJobService.updateTime(uuid, (int) ((time)/1000),database);
  }
	public String fileCheck(MultipartFile  file, String database){
		String uuid=null;
		try {
			if(file.getOriginalFilename().equals("")||file.getOriginalFilename()==null){
				log.error("文件不存在");
				return null;
			}
			FileUtil.check(file.getOriginalFilename());
			UUID randomUUID = UUID.randomUUID();
			uuid = randomUUID.toString();
			scmNretailJobService.add(file.getOriginalFilename(),uuid,database);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return uuid;
	}


	public List<ScmNretail> ReadExcel(MultipartFile file, String uuid, String database,int weekStart){
		List<ScmNretail> listscmNretails = new ReadExcel().getExcelInfo(file, weekStart);
		ScmNretailJob job = new ScmNretailJob();
		if(listscmNretails==null){
			job.setState("fail");
			job.setUuid(uuid);
			job.setJobinfo("文件格式错误，请重新上传!");
			job.setEndtime(new Date());
			log.error("文件格式错误，请重新上传!");
			scmNretailJobService.update(job,database);
		}
		return listscmNretails;
	}
	public List<ScmNretail> readExcel(String sourceFilePath,String fileName, String uuid, String database,Integer weekStart){
		if(fileName==null||fileName==""){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			fileName = "scm_nretail_"+sdf.format(new Date())+".xlsx";
		}
		File file = new File(sourceFilePath+fileName);
		if(weekStart==null){
			weekStart=0;
		}
		List<ScmNretail> listscmNretails = new ReadExcel().getExcelInfo(file, weekStart);
		ScmNretailJob job = new ScmNretailJob();
		if(listscmNretails==null){
			job.setState("fail");
			job.setUuid(uuid);
			job.setJobinfo("文件格式错误，请重新上传!");
			job.setEndtime(new Date());
			log.error("文件格式错误，请重新上传!");
			scmNretailJobService.update(job,database);
		}else {
			job.setUuid(uuid);
			job.setJobinfo("文件格式校验通过!");
			log.info("文件格式校验通过!");
			scmNretailJobService.update(job,database);
		}
		return listscmNretails;
	}
	public Boolean saveExcel(String uuid,List<ScmNretail> listscmNretails, String database){
		ScmNretailJob job = new ScmNretailJob();
		try {
			scmNretailService.truncate(database);
			scmNretailService.save(listscmNretails, database);
			job.setUuid(uuid);
			job.setJobinfo("原始数据入库成功！数据行数:" + listscmNretails.size());
			scmNretailJobService.update(job,database);
			log.info("原始数据入库成功！数据行数:" + listscmNretails.size());
			return true;
		}catch (Exception e){
			e.printStackTrace();
			job.setUuid(uuid);
			job.setState("fail");
			job.setJobinfo("原始数据入库失败");
			job.setEndtime(new Date());
			scmNretailJobService.update(job,database);
			return false;
		}
	}
	public List<ScmNretailIpw> countByGpw(String uuid,String database, String fileName, Integer weekStart){
		ScmNretailJob job = new ScmNretailJob();
		try {
			List<ScmNretailIpw> scmNretailIpds = scmNretailService.countByGpw(database);
			log.info("销量数据汇总成功！数据行数:" + scmNretailIpds.size());
			scmNretailIpds = scmNretailService.dsList2(scmNretailIpds,database, fileName, weekStart);
			log.info("缺失日期补全成功！数据行数:" + scmNretailIpds.size());
			job.setUuid(uuid);
			job.setJobinfo("销量数据汇总补全成功！数据行数:" + scmNretailIpds.size());
			scmNretailJobService.update(job,database);
			return scmNretailIpds;
		}catch (Exception e){
			e.printStackTrace();
			job.setUuid(uuid);
			job.setState("fail");
			job.setJobinfo("销量数据汇总失败");
			job.setEndtime(new Date());
			scmNretailJobService.update(job,database);
			return null;
		}

	}
	public Boolean saveGpwData(String uuid,List<ScmNretailIpw> scmNretailIpds,String database){
		boolean save = scmNretailIpwService.save(uuid, scmNretailIpds, database);
		if(save){
			log.info("汇总数据入库成功！数据行数:" + scmNretailIpds.size());
			ScmNretailJob job = new ScmNretailJob();
			job.setUuid(uuid);
			job.setJobinfo("汇总数据入库成功！数据行数:" + scmNretailIpds.size());
			scmNretailJobService.update(job,database);
		}
		return save;
	}
//将汇总数据平滑处理后拆成2份
	public List<ScmNretailIpw> preGpwData(String uuid,List<ScmNretailIpw> scmNretailIpds,String database){
		ScmNretailJob job = new ScmNretailJob();
		try {
			List<ScmNretailIpw> list =new ArrayList<ScmNretailIpw>();
			for (ScmNretailIpw scm:scmNretailIpds) {
				ScmNretailIpw scmNretailIpw = new ScmNretailIpw();
				scmNretailIpw.setMatnr(scm.getMatnr());
				scmNretailIpw.setCnt(scm.getCnt());
				scmNretailIpw.setDs(scm.getDs());
				scmNretailIpw.setProv(scm.getProv());
				list.add(scmNretailIpw);
			}
			//	异常值平滑处理
			List<ScmNretailIpw> list1 = preGpw(scmNretailIpds,database);
			list.addAll(list1);
			log.info("异常值平滑处理成功！数据行数:" + list.size());
			return list;
		}catch (Exception e){
			job.setUuid(uuid);
			job.setState("fail");
			job.setJobinfo("异常值平滑处理失败");
			job.setEndtime(new Date());
			scmNretailJobService.update(job,database);
			e.printStackTrace();
			return null;
		}

	}
	public List<ScmNretailIpw> preGpwData2(String uuid,List<ScmNretailIpw> scmNretailIpds,String database){
		ScmNretailJob job = new ScmNretailJob();
		try {
			List<ScmNretailIpw> scm = FileUtil.deepCopy(scmNretailIpds);
			//	异常值平滑处理
			List<ScmNretailIpw> list = preGpw2(scmNretailIpds,database);
			scm.addAll(list);
			log.info("异常值平滑处理成功！数据行数:" + list.size());
			job.setUuid(uuid);
			job.setJobinfo("异常值平滑处理成功！数据行数:" + list.size());
			scmNretailJobService.update(job,database);
			return scm;
		}catch (Exception e){
			job.setUuid(uuid);
			job.setState("fail");
			job.setJobinfo("异常值平滑处理失败");
			job.setEndtime(new Date());
			scmNretailJobService.update(job,database);
			e.printStackTrace();
			return null;
		}

	}
	//	异常值平滑处理
	public List<ScmNretailIpw> preGpw(List<ScmNretailIpw> scmNretailIpds,String database) {
		List<ScmNretailDsVo> scmNretailDsVos = scmNretailService.naProList(database);
		List<ScmNretailIpw> ipws = new ArrayList<>();
		for (ScmNretailDsVo vo:scmNretailDsVos) {
			List<ScmNretailIpw> list = new ArrayList<ScmNretailIpw>();
			String prov = vo.getProv();
			String matnr = vo.getMatnr();
			for (ScmNretailIpw ipw:scmNretailIpds ) {
				if(ipw.getMatnr().equals(matnr)&&ipw.getProv().equals(prov)){
					list.add(ipw);
				}
			}
			Double pre = null;
			for (int i = 0; i < list.size(); i++) {
				ScmNretailIpw scmNretailIpw = list.get(i);
				String ma = scmNretailIpw.getMatnr();
				char[] items=ma.toCharArray();
				items[items.length-1]='X';
				String replace = String.valueOf(items);
				scmNretailIpw.setMatnr(replace);
				if(i!=0 && i!=list.size()-1){
					if(pre==null){
						pre=list.get(i-1).getCnt();
					}
					Double cur = scmNretailIpw.getCnt();
					Double after = list.get(i+1).getCnt();
					double rate =Math.abs((cur - pre) / pre) ;
					if(rate>0.85){
						double avg = (pre + after) / 2;
						scmNretailIpw.setCnt(avg);
					}
					pre=cur;
				}
			}
			ipws.addAll(list);
		}
		return ipws;
	}
	//	异常值平滑处理
	public List<ScmNretailIpw> preGpw2(List<ScmNretailIpw> scmNretailIpds,String database) {
		int i=0;
		ScmNretailIpw scm = (ScmNretailIpw) SerializationUtils.clone(scmNretailIpds.get(i));
		String prov = scm.getProv();
		String matnr = scm.getMatnr();
		scmNretailIpds.get(i).setMatnr("X"+matnr.substring(1));
			for ( i = 1; i < scmNretailIpds.size(); i++) {
				ScmNretailIpw scmCur = scmNretailIpds.get(i);
				if( i==scmNretailIpds.size()-1){
					scmCur.setMatnr("X"+scmCur.getMatnr().substring(1));
					break;
				}
				ScmNretailIpw scmPre = new ScmNretailIpw();
				scmPre.setProv(prov);
				scmPre.setMatnr(matnr);
				scmPre.setDs(scm.getDs());
				scmPre.setCnt(scm.getCnt());
				ScmNretailIpw scmAfter = scmNretailIpds.get(i+1);
				if(prov.equals(scmAfter.getProv())&&matnr.equals(scmAfter.getMatnr())&&prov.equals(scmPre.getProv())&&matnr.equals(scmPre.getMatnr())){
					scmCur.setMatnr("X"+scmCur.getMatnr().substring(1));
					Double pre = scmPre.getCnt();
					Double cur = scmCur.getCnt();
					Double after = scmAfter.getCnt();
					double rate =Math.abs((cur - pre) / pre) ;
					if(rate>0.85){
						double avg = (pre + after) / 2;
						scmCur.setCnt(avg);
					}
					scm.setDs(scmCur.getDs());
					scm.setCnt(cur);

				}else {
					prov=scmCur.getProv();
					matnr=scmCur.getMatnr();
					scm.setDs(scmCur.getDs());
					scm.setCnt(scmCur.getCnt());
					scmCur.setMatnr("X"+scmCur.getMatnr().substring(1));
				}

			}
		return scmNretailIpds;
	}
	public void predict(Integer startnum,String readFilePath,ArrayList<String> readfileNames,String outFilePath,String logFilePath,String pythonCommand,String beforeCommand,String uuid,String database){
		if(startnum==null){
			startnum=44;
		}
		scmNretailIpwResultService.truncate();
		ArrayList<Future<Boolean>> list = new ArrayList<>();
		ScmNretailResultServiceImpl.fail_num=0;
		ScmNretailResultServiceImpl.success_num=0;
		ScmNretailResultServiceImpl.presuccess_num=0;
		ScmNretailResultServiceImpl.prefail_num=0;
		for (String readfileName:readfileNames) {
//			Future<Boolean> future = pc.predict(latch,readFilePath, readfileName, outFilePath, pythonCommand, beforeCommand,startnum);
			Future<Boolean> future = scmNretailResultService.predict(uuid,readFilePath, readfileName, outFilePath, logFilePath, pythonCommand, beforeCommand,startnum);
			list.add(future);
		}
		ScmNretailJob job = new ScmNretailJob();
		job.setUuid(uuid);
		scmNretailJobService.updateState(job,database,list);
	}
	@Async("asyncServiceExecutor")
	public void updateState(CountDownLatch latch,String uuid,String database ,ArrayList<Future<Boolean>> list) {
  		Boolean b=true;
		ScmNretailJob job = new ScmNretailJob();
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
				scmNretailJobService.update(job,database);
				log.info("预测任务成功");
			}else {
				log.info("预测任务失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("预测任务失败");
		}

	}

	/**
      * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response,@RequestParam(name = "weekStart", required = false) Integer  weekStart) {
	  Map<String,String> resource =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\dataPath.properties");
	  String sourceFilePath = resource.get("sourceFilePath");
	  String readFilePath = resource.get("readFilePath");
	  String outFilePath = resource.get("outFilePath");
	  String pythonCommand = resource.get("pythonCommand");
	  String beforeCommand = resource.get("beforeCommand");
	  String logFilepath = resource.get("logFilepath");
	  Map<String,String> resource2 =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\jeecg_database.properties");
	  String database = resource2.get("database");
	  MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
	  scmNretailService.truncate(database);
		if(weekStart==null){
			weekStart=0;
		}
	  String uuid = null;
	  long startTime = System.currentTimeMillis();
	  try{
			  if(fileMap.isEmpty()||fileMap.size()==0){
				  log.error("没有上传文件");
//				  return Result.error("没有上传文件");
			  }
			for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
				 startTime = System.currentTimeMillis();
				MultipartFile file = entity.getValue();// 获取上传文件对象
//				创建任务
				 uuid = fileCheck(file,database);
				if (uuid == null) {	break;}
//				  数据预处理
				List<ScmNretail> listscmNretails = ReadExcel(file, uuid, database, weekStart);
				if(listscmNretails==null){break;}
//				原始数据入库
				Boolean aBoolean1 = saveExcel(uuid, listscmNretails, database);
				if(!aBoolean1){break;}
//				 拆分汇总
				String fileName="";
				List<ScmNretailIpw> scmNretailIpds = countByGpw(uuid,database,fileName, weekStart);
				if(scmNretailIpds==null){break;}
				//异常数据识别，平滑处理后分成2份
				scmNretailIpds = preGpwData(uuid,scmNretailIpds, database);
				if(scmNretailIpds==null){break;}
//				汇总数据入库
				Boolean aBoolean = saveGpwData(uuid, scmNretailIpds, database);
				if(!aBoolean){break;}
//				导出csv
				ArrayList<String> readfileNames = exportCsv(uuid,scmNretailIpds,readFilePath,database);
				if(readfileNames==null){break;}
//		  		调用python脚本预测
				 predict(44,readFilePath, readfileNames, outFilePath,  logFilepath,pythonCommand, beforeCommand, uuid, database);
			}

	  }catch (Exception e) {
		  ScmNretailJob job = new ScmNretailJob();
		  job.setUuid(uuid);
		  job.setState("fail");
		  scmNretailJobService.update(job,database);
		  log.error(e.getMessage(),e);
	  }finally {
		  long endtime = System.currentTimeMillis();
		  scmNretailJobService.updateTime(uuid, (int) ((endtime-startTime)/1000),database);
		  ScmNretailJob job = new ScmNretailJob();
		  job.setUuid(uuid);
		  return jobList( database, job);
	  }

  }
	@RequestMapping(value = "/jobList", method = RequestMethod.GET)
	public Result<?> jobList(@RequestParam(name = "database", defaultValue = "mysql") String database, ScmNretailJob job) {
		List<Map<String, Object>> list = scmNretailJobService.jobList(job,database);
		return Result.ok(list);
	}

	/**
	  * 读取路径下文件
	  *
	  * @return
	  */
	 @RequestMapping(value = "/startTask", method = RequestMethod.POST)
	 @Async("asyncServiceExecutor")
	 public void startTask(String fileName,String uuid,Integer weekStart,Integer startnum) {
		 Map<String,String> resource =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\dataPath.properties");
		 String sourceFilePath = resource.get("sourceFilePath");
		 String readFilePath = resource.get("readFilePath");
		 String outFilePath = resource.get("outFilePath");
		 String logFilepath = resource.get("logFilePath");
		 String pythonCommand = resource.get("pythonCommand");
		 String beforeCommand = resource.get("beforeCommand");
		 Map<String,String> resource2 =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\jeecg_database.properties");
		 String database = resource2.get("database");
		 try{
	//	数据内容校验
			 List<ScmNretail> listscmNretails = readExcel(sourceFilePath, fileName, uuid, database, weekStart);
			 if(listscmNretails==null){return ;}
	//	原始数据入库
			 Boolean aBoolean1 = saveExcel(uuid, listscmNretails, database);
			 if(!aBoolean1){return ;}
	//	拆分汇总
			 List<ScmNretailIpw> scmNretailIpds = countByGpw(uuid,database, fileName, weekStart);
			 if(scmNretailIpds==null){return ;}
	 //异常数据识别，平滑处理后分成2份
			 scmNretailIpds = preGpwData2(uuid,scmNretailIpds, database);
			 if(scmNretailIpds==null){return ;}
	//	汇总数据入库
			 Boolean aBoolean = saveGpwData(uuid, scmNretailIpds, database);
			 if(!aBoolean){return ;}
	//	导出csv
			 ArrayList<String> readfileNames = exportCsv(uuid,scmNretailIpds,readFilePath,database);
			 if(readfileNames==null){return ;}
	//	调用python脚本预测
			 predict(startnum,readFilePath, readfileNames, outFilePath, logFilepath,pythonCommand, beforeCommand,  uuid, database);

		 }catch (Exception e) {
			 ScmNretailJob job = new ScmNretailJob();
			 job.setUuid(uuid);
			 job.setState("fail");
			 job.setEndtime(new Date());
			 scmNretailJobService.update(job,database);
			 log.error(e.getMessage(),e);
		 }
	 }

//	 导出csv输入模型文件
	 public ArrayList<String> exportCsv(String uuid,List<ScmNretailIpw> list,String writePath,String database){
		 ArrayList<String> readfileNames = null;
		 try {
			 List<ScmNretailIpw> scmNretailIpds= scmNretailIpwService.getAll(database);
//			 按照一个物料、一个省份为一个文件拆分汇总数据
//			 List<ScmNretailpiVo> scmNretailpiVos = scmNretailIpwService.countByGpw(database);
			 readfileNames=CsvUtil.exportCsv2(scmNretailIpds,writePath);
			 log.info("导出CSV文件成功");
			 ScmNretailJob job = new ScmNretailJob();
			 job.setUuid(uuid);
			 job.setJobinfo("导出CSV文件成功");
			 scmNretailJobService.update(job,database);
		 } catch (IOException e) {
			 ScmNretailJob job = new ScmNretailJob();
			 job.setUuid(uuid);
			 job.setState("fail");
			 job.setJobinfo("导出CSV文件失败");
			 job.setEndtime(new Date());
			 scmNretailJobService.update(job,database);
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
