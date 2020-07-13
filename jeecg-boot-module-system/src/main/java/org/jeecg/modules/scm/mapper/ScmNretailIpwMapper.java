package org.jeecg.modules.scm.mapper;

import org.jeecg.modules.predict.entity.ScmNretailIpw;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.predict.vo.ScmNretailIpdVo;
import org.jeecg.modules.scm.vo.ScmNretailpiVo;

import java.util.List;

/**
 * @Description: 销量统计按天
 * @Author: jeecg-boot
 * @Date:   2020-03-11
 * @Version: V1.0
 */
public interface ScmNretailIpwMapper extends BaseMapper<ScmNretailIpw> {

    public boolean save(List<ScmNretailIpw> scmNretailIpds);

    public void truncate();
    public List<ScmNretailpiVo> countByGpw();
    public void saveOne(ScmNretailIpw scmNretailIpw);
    public List<ScmNretailIpw>  getAll();
}
