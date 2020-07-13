package org.jeecg.modules.scm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.predict.entity.ScmNretailIpw;

import org.jeecg.modules.predict.vo.ScmNretailIpdVo;
import org.jeecg.modules.scm.vo.ScmNretailpiVo;

import java.util.List;

/**
 * @Description: 销量统计按天
 * @Author: jeecg-boot
 * @Date:   2020-03-11
 * @Version: V1.0
 */
public interface ScmNretailIpwService extends IService<ScmNretailIpw> {
    public void truncate(String database);
    public boolean save(String uuid, List<ScmNretailIpw> lists, String database);
    public List<ScmNretailIpw>  getAll(String database);
    public List<ScmNretailpiVo> countByGpw(String database);
    public void saveOne(ScmNretailIpw scmNretailIpw);
}
