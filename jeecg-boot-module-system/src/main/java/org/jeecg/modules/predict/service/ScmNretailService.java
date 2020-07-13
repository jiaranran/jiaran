package org.jeecg.modules.predict.service;

import org.jeecg.modules.predict.entity.ScmNretail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.predict.entity.ScmNretailIpw;
import org.jeecg.modules.predict.vo.ScmNretailDsVo;
import org.jeecg.modules.predict.vo.ScmNretailIpdVo;
import org.jeecg.modules.result.entity.ScmNretailResult;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * @Description: 原始数据
 * @Author: jeecg-boot
 * @Date:   2020-03-14
 * @Version: V1.0
 */
public interface ScmNretailService extends IService<ScmNretail> {

    public void truncate(String database);
    public void save(List<ScmNretail> listscmNretails, String database);
    public void save(List<ScmNretail> listscmNretails, File file);
    public List<ScmNretailIpdVo> countByIpd();
    public List<ScmNretailIpdVo> countByIpw();
    public List<ScmNretailIpdVo> countByGpd();
    public List<ScmNretailIpw> countByGpw(String database);
    public List<ScmNretailIpw> dsList(List<ScmNretailIpw> scmNretailIpdList, String database);
    public List<ScmNretailIpw> countByGpwMo();
    public void saveMo(List<ScmNretail> listscmNretails);
    public void truncateMo();
    public List<ScmNretailDsVo> naProList(String database);
    List<ScmNretailIpw> dsList2(List<ScmNretailIpw> scmNretailIpds, String database, String fileName, Integer weekStart);
}
