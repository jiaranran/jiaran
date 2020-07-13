package org.jeecg.modules.stkpredict.service;
import org.jeecg.modules.stkpredict.entity.ScmNretailIpd;
import org.jeecg.modules.stkpredict.entity.ScmNretailStk;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 原始数据
 * @Author: jeecg-boot
 * @Date:   2020-07-06
 * @Version: V1.0
 */
public interface IScmNretailStkService extends IService<ScmNretailStk> {
    public void truncate(String database);
    public void save(List<ScmNretailStk> listscmNretails, String database);
    public List<ScmNretailIpd> countByGpw(String database);
    List<ScmNretailIpd> dsList2(List<ScmNretailIpd> scmNretailIpds, String database, String fileName);
}
