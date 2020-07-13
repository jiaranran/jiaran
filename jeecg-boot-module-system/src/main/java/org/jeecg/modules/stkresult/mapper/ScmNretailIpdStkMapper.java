package org.jeecg.modules.stkresult.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.result.entity.ScmNretailResult;
import org.jeecg.modules.result.vo.ScmNretailQuotaVo;
import org.jeecg.modules.result.vo.ScmNretailResultVo;
import org.jeecg.modules.stkresult.entity.ScmNretailIpdStk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 安全库存结果
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
public interface ScmNretailIpdStkMapper extends BaseMapper<ScmNretailIpdStk> {
    public void truncate();
    public List<ScmNretailIpdStk> list(ScmNretailIpdStk scmNretailResult);
    public boolean update();
}
