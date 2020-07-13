package org.jeecg.modules.stkpredict.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jeecg.modules.predict.vo.ScmNretailDsVo;
import org.jeecg.modules.predict.vo.ScmNretailIpdVo;
import org.jeecg.modules.stkpredict.entity.ScmNretailIpd;
import org.jeecg.modules.stkpredict.entity.ScmNretailStk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 原始数据
 * @Author: jeecg-boot
 * @Date:   2020-07-06
 * @Version: V1.0
 */
public interface ScmNretailStkMapper extends BaseMapper<ScmNretailStk> {
    public List<ScmNretailIpdVo> countByIpd();

    public List<ScmNretailIpdVo> countByIpw();

    public List<ScmNretailIpdVo> countByGpd();

    public List<ScmNretailIpd> countByGpw();

    public List<ScmNretailDsVo> getDs();

    public List<Date> findDsList(Map map);

    public void truncate();

}
