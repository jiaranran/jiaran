package org.jeecg.modules.scm.controller;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.jeecg.modules.predict.entity.ScmNretailIpw;

import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.scm.service.impl.ScmNretailIpwServiceImpl;
import org.jeecg.modules.scm.vo.ScmNretailpiVo;
import org.jeecg.modules.util.ResouceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 销量统计按天
 * @Author: jiaran
 * @Date:   2020-03-11
 * @Version: V1.0
 */
@Slf4j
@Api(tags="销量统计按天")
@RestController
@RequestMapping("/scm/scmNretailIpd")
public class ScmNretailIpwController {
	@Autowired
	private ScmNretailIpwServiceImpl scmNretailIpdService;
	@Autowired
	ResourceLoader resourceLoader;
	/**
	 * 导出excel
	 *
	 * @param response
	 */
	/*@
	RequestMapping(value = "/exportCsv")
	public void exportCsv(HttpServletResponse response) throws IOException {
		List<ScmNretailIpw> scmNretailIpds=scmNretailIpdService.getAll(database);
		List<ScmNretailpiVo> scmNretailpiVos = scmNretailIpdService.countByGpw(database);
		Map<String,String> resource =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\dataPath.properties");
		String readFilePath = resource.get("readFilePath");
		for (ScmNretailpiVo vo:scmNretailpiVos ) {
			String matnr = vo.getMatnr();
			String prov = vo.getProv();
			String fileName=readFilePath+matnr+"_"+prov+".csv";
			File file = new File(fileName);
			//构建输出流，同时指定编码
			OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");

			//写内容
			for(ScmNretailIpw scmNretailIpd:scmNretailIpds){
				if(scmNretailIpd.getMatnr().equals(matnr)&&scmNretailIpd.getProv().equals(prov)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					String ds = format.format(scmNretailIpd.getDs());
					ow.write('\t'+scmNretailIpd.getMatnr());
					ow.write(",");
					ow.write(scmNretailIpd.getProv());
					ow.write(",");
					ow.write(ds);
					ow.write(",");
//					ow.write(scmNretailIpd.getCnt().toString());
					ow.write(",");
					//写完一行换行
					ow.write("\r\n");
				}
				//利用反射获取所有字段

			}
			ow.flush();
			ow.close();
		}


	}*/



}
