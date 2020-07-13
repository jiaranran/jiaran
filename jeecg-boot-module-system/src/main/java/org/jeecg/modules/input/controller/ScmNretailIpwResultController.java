package org.jeecg.modules.input.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.input.entity.ScmNretailIpwResult;
import org.jeecg.modules.input.service.IScmNretailIpwResultService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 输入文件预测结果
 * @Author: jeecg-boot
 * @Date:   2020-06-11
 * @Version: V1.0
 */
@Slf4j
@Api(tags="输入文件预测结果")
@RestController
@RequestMapping("/input/scmNretailIpwResult")
public class ScmNretailIpwResultController {
	@Autowired
	private IScmNretailIpwResultService scmNretailIpwResultService;
	
	/**
	  * 分页列表查询
	 * @param scmNretailIpwResult
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "输入文件预测结果-分页列表查询")
	@ApiOperation(value="输入文件预测结果-分页列表查询", notes="输入文件预测结果-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ScmNretailIpwResult>> queryPageList(ScmNretailIpwResult scmNretailIpwResult,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<ScmNretailIpwResult>> result = new Result<IPage<ScmNretailIpwResult>>();
		QueryWrapper<ScmNretailIpwResult> queryWrapper = QueryGenerator.initQueryWrapper(scmNretailIpwResult, req.getParameterMap());
		Page<ScmNretailIpwResult> page = new Page<ScmNretailIpwResult>(pageNo, pageSize);
		IPage<ScmNretailIpwResult> pageList = scmNretailIpwResultService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param scmNretailIpwResult
	 * @return
	 */
	@AutoLog(value = "输入文件预测结果-添加")
	@ApiOperation(value="输入文件预测结果-添加", notes="输入文件预测结果-添加")
	@PostMapping(value = "/add")
	public Result<ScmNretailIpwResult> add(@RequestBody ScmNretailIpwResult scmNretailIpwResult) {
		Result<ScmNretailIpwResult> result = new Result<ScmNretailIpwResult>();
		try {
			scmNretailIpwResultService.save(scmNretailIpwResult);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param scmNretailIpwResult
	 * @return
	 */
	@AutoLog(value = "输入文件预测结果-编辑")
	@ApiOperation(value="输入文件预测结果-编辑", notes="输入文件预测结果-编辑")
	@PutMapping(value = "/edit")
	public Result<ScmNretailIpwResult> edit(@RequestBody ScmNretailIpwResult scmNretailIpwResult) {
		Result<ScmNretailIpwResult> result = new Result<ScmNretailIpwResult>();
		ScmNretailIpwResult scmNretailIpwResultEntity = scmNretailIpwResultService.getById(scmNretailIpwResult.getId());
		if(scmNretailIpwResultEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = scmNretailIpwResultService.updateById(scmNretailIpwResult);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "输入文件预测结果-通过id删除")
	@ApiOperation(value="输入文件预测结果-通过id删除", notes="输入文件预测结果-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<ScmNretailIpwResult> delete(@RequestParam(name="id",required=true) String id) {
		Result<ScmNretailIpwResult> result = new Result<ScmNretailIpwResult>();
		ScmNretailIpwResult scmNretailIpwResult = scmNretailIpwResultService.getById(id);
		if(scmNretailIpwResult==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = scmNretailIpwResultService.removeById(id);
			if(ok) {
				result.success("删除成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "输入文件预测结果-批量删除")
	@ApiOperation(value="输入文件预测结果-批量删除", notes="输入文件预测结果-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<ScmNretailIpwResult> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<ScmNretailIpwResult> result = new Result<ScmNretailIpwResult>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.scmNretailIpwResultService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "输入文件预测结果-通过id查询")
	@ApiOperation(value="输入文件预测结果-通过id查询", notes="输入文件预测结果-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ScmNretailIpwResult> queryById(@RequestParam(name="id",required=true) String id) {
		Result<ScmNretailIpwResult> result = new Result<ScmNretailIpwResult>();
		ScmNretailIpwResult scmNretailIpwResult = scmNretailIpwResultService.getById(id);
		if(scmNretailIpwResult==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(scmNretailIpwResult);
			result.setSuccess(true);
		}
		return result;
	}

  /**
      * 导出excel
   *
   * @param request
   * @param response
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
      // Step.1 组装查询条件
      QueryWrapper<ScmNretailIpwResult> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              ScmNretailIpwResult scmNretailIpwResult = JSON.parseObject(deString, ScmNretailIpwResult.class);
              queryWrapper = QueryGenerator.initQueryWrapper(scmNretailIpwResult, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<ScmNretailIpwResult> pageList = scmNretailIpwResultService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "输入文件预测结果列表");
      mv.addObject(NormalExcelConstants.CLASS, ScmNretailIpwResult.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("输入文件预测结果列表数据", "导出人:Jeecg", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
  }

  /**
      * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<ScmNretailIpwResult> listScmNretailIpwResults = ExcelImportUtil.importExcel(file.getInputStream(), ScmNretailIpwResult.class, params);
              for (ScmNretailIpwResult scmNretailIpwResultExcel : listScmNretailIpwResults) {
                  scmNretailIpwResultService.save(scmNretailIpwResultExcel);
              }
              return Result.ok("文件导入成功！数据行数:" + listScmNretailIpwResults.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("文件导入失败！");
  }

}
