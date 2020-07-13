package org.jeecg.modules.result.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.result.entity.ScmNretailResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.result.vo.ScmNretailQuotaVo;
import org.jeecg.modules.result.vo.ScmNretailResultVo;

/**
 * @Description: 预测结果
 * @Author: jeecg-boot
 * @Date:   2020-03-16
 * @Version: V1.0
 */
public interface ScmNretailResultMapper extends BaseMapper<ScmNretailResult> {

    public void truncate();

    public List<ScmNretailResultVo> list(ScmNretailResult scmNretailResult);
    public List<ScmNretailResult> list2(ScmNretailResult scmNretailResult);
    public boolean update();

    public List<ScmNretailQuotaVo> countByEval();

    public List<Map<String,Object>> evalList();

    public List<Map<String,Object>> evalpmList(Map<String, String> map);

    public List<ScmNretailResult> getResult(Map<String, Object> map);
}
