package org.jeecg.modules.stkresult.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import org.assertj.core.util.DateUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.stkresult.entity.ScmNretailIpdStk;
import org.jeecg.modules.stkresult.service.IScmNretailIpdStkService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.util.ResouceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;


 /**
 * @Description: 安全库存结果
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
@Slf4j
@Api(tags="安全库存结果")
@RestController
@RequestMapping("/stkresult/scmNretailIpdStk")
public class ScmNretailIpdStkController {
	@Autowired
	private IScmNretailIpdStkService scmNretailIpdStkService;
	 @Autowired
	 ResourceLoader resourceLoader;
	 /**
	  * 获取结果列表
	  * @return
	  */
	 @RequestMapping(value = "/resultLists", method = RequestMethod.GET)
	 public Result<?> resultLists(ScmNretailIpdStk result) {
		 Map<String,String> resource2 =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\jeecg_database.properties");
		 String database = resource2.get("database");
		 List<ScmNretailIpdStk> scmNretailResultVos = scmNretailIpdStkService.resultLists(database,result);
		 return Result.ok(scmNretailResultVos);
	 }

 }
