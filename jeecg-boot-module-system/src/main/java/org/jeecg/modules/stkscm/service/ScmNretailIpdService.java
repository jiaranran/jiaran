package org.jeecg.modules.stkscm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.stkscm.vo.ScmNretailpiVo;
import org.jeecg.modules.stkpredict.entity.ScmNretailIpd;

import java.util.List;

/**
 * @Description: 销量统计按天
 * @Author: jeecg-boot
 * @Date:   2020-03-11
 * @Version: V1.0
 */
public interface ScmNretailIpdService extends IService<ScmNretailIpd> {
    public void truncate(String database);
    public boolean save(String uuid, List<ScmNretailIpd> lists, String database);
    public List<ScmNretailIpd>  getAll(String database);
    public List<ScmNretailpiVo> countByGpw(String database);
    public void saveOne(ScmNretailIpd scmNretailIpw);
}
