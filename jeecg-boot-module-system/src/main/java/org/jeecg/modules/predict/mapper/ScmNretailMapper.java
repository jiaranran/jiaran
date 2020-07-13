package org.jeecg.modules.predict.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jeecg.modules.predict.entity.ScmNretail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.predict.entity.ScmNretailIpw;

import org.jeecg.modules.predict.vo.ScmNretailDsVo;
import org.jeecg.modules.predict.vo.ScmNretailIpdVo;

/**
 * @Description: 原始数据
 * @Author: jeecg-boot
 * @Date:   2020-03-14
 * @Version: V1.0
 */
public interface ScmNretailMapper extends BaseMapper<ScmNretail> {
    public List<ScmNretailIpdVo> countByIpd();

    public List<ScmNretailIpdVo> countByIpw();

    public List<ScmNretailIpdVo> countByGpd();

    public List<ScmNretailIpw> countByGpw();

    public List<ScmNretailDsVo> getDs();

    public List<Date> findDsList(Map map);

    public void truncate();


}
