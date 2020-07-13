package org.jeecg.modules.result.controller;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.csvreader.CsvReader;
import org.apache.commons.lang.time.DateFormatUtils;
import org.assertj.core.util.DateUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.result.entity.ScmNretailResult;
import org.jeecg.modules.result.service.IScmNretailResultService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.result.vo.ScmNretailQuotaVo;
import org.jeecg.modules.result.vo.ScmNretailResultVo;
import org.jeecg.modules.util.FileUtil;
import org.jeecg.modules.util.ResouceUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;

/**
 * @Description: 预测结果
 * @Author: jiaran
 * @Date:   2020-03-16
 * @Version: V1.0
 */
@Slf4j
@Api(tags="预测结果")
@RestController
@RequestMapping("/result/scmNretailResult")
public class ScmNretailResultController {
     @Autowired
     private IScmNretailResultService scmNretailResultService;
     @Autowired
     ResourceLoader resourceLoader;

    @RequestMapping(value = "/evalList", method = RequestMethod.GET)
     public Result<?> evalList(@RequestParam(name = "matnr") String matnr,@RequestParam(name = "prov") String prov){
        Map<String,String> resource2 =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\jeecg_database.properties");
        String database = resource2.get("database");
        List<Map<String, Object>> list = scmNretailResultService.evalpmList(matnr, prov, database);
        return Result.ok(list) ;
     }
     /**
      * 获取结果列表
      * @return
      */
     @RequestMapping(value = "/resultLists", method = RequestMethod.GET)
     public Result<?> resultLists(@RequestParam(name = "matnr",required = false) String matnr,@RequestParam(name = "prov",required = false) String prov,@RequestParam(name = "ds",required = false) String ds) {
         Map<String,String> resource2 =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\jeecg_database.properties");
         String database = resource2.get("database");
         ScmNretailResult result = new ScmNretailResult();
         result.setMatnr(matnr);
         result.setProv(prov);
         if(ds!=null&&!ds.equals("")){
             Date date = DateUtil.parse(ds);
             result.setDs(date);
         }
         List<ScmNretailResultVo> scmNretailResultVos = scmNretailResultService.resultLists(database,result);
         List<ScmNretailQuotaVo> scmNretailQuotaVos = scmNretailResultService.countByEval(database);
//         List<Map<String, Object>> list = scmNretailResultService.evalList();
//         HashMap<String, Object> map = new HashMap<>();
//         map.put("resList",scmNretailResultVos);
//         map.put("quota",scmNretailQuotaVos.get(0));
//         map.put("evalList",list);
         return Result.ok(scmNretailResultVos);
     }


     /**
      * 读取预测结果文件
      * @return
      */
	 @RequestMapping(value = "/readResFile", method = RequestMethod.GET)
	 public Result<?> getFile() {
         Map<String,String> resource =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\dataPath.properties");
         String outFilePath = resource.get("outFilePath");
         Map<String,String> resource2 =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\jeecg_database.properties");
         String database = resource2.get("database");
         try {
         List<File> fileList = FileUtil.getFileList(outFilePath, true);
		 scmNretailResultService.truncate(database);
		 for (File file : fileList) {
                 ArrayList<ScmNretailResult> list = new ArrayList<>();
                 CsvReader csvReader = new CsvReader(outFilePath+file.getName(), ',', Charset.forName("UTF-8"));
                 // 如果你的文件没有表头，这行不用执行
                 // 这行不要是为了从表头的下一行读，也就是过滤表头
                 csvReader.readHeaders();
                 while (csvReader.readRecord()) {
                     ScmNretailResult scmNretailResult = new ScmNretailResult();
                     scmNretailResult.setMatnr(csvReader.get("matnr"));
                     scmNretailResult.setProv(csvReader.get("prov"));
                     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                     Date ds = DateUtils.str2Date(csvReader.get("ds"), simpleDateFormat);
                     scmNretailResult.setDs(ds);
                     scmNretailResult.setPred(Double.parseDouble(csvReader.get("pre_cnt")));
                     list.add(scmNretailResult);

				 }
				 scmNretailResultService.save(list, database);
			 }
		 }catch (Exception e) {
             e.printStackTrace();
             return Result.error("文件导入失败:"+e.getMessage());
         }
		 return Result.ok("文件导入成功！");
	 }


}
