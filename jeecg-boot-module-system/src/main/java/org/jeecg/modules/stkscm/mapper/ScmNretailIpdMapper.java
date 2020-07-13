package org.jeecg.modules.stkscm.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.stkscm.vo.ScmNretailpiVo;
import org.jeecg.modules.stkpredict.entity.ScmNretailIpd;

import java.util.List;

/**
 * @Description: 销量统计按天
 * @Author: jeecg-boot
 * @Date:   2020-03-11
 * @Version: V1.0
 */
public interface ScmNretailIpdMapper extends BaseMapper<ScmNretailIpd> {

    public boolean save(List<ScmNretailIpd> scmNretailIpds);

    public void truncate();
    public List<ScmNretailpiVo> countByGpw();
    public void saveOne(ScmNretailIpd scmNretailIpw);
    public List<ScmNretailIpd>  getAll();
}
