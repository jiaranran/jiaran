package org.jeecg.modules.time.service.impl;

import org.jeecg.modules.predict.entity.ScmNretail;
import org.jeecg.modules.predict.entity.ScmNretailIpw;
import org.jeecg.modules.time.entity.ScmNretailTime;
import org.jeecg.modules.time.mapper.ScmNretailTimeMapper;
import org.jeecg.modules.time.service.IScmNretailTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 提前期
 * @Author: jeecg-boot
 * @Date:   2020-06-22
 * @Version: V1.0
 */
@Service
public class ScmNretailTimeServiceImpl extends ServiceImpl<ScmNretailTimeMapper, ScmNretailTime> implements IScmNretailTimeService {
    @Autowired
    ScmNretailTimeMapper scmNretailTimeMapper;
    @Transactional
    @Override
    public void save(List<ScmNretailTime> scmTimes, String database) {
        if(database.equals("mysql")){
            super.saveBatch(scmTimes);
        }

    }
    @Override
    public void truncate(String database) {
        if(database.equals("mysql")){
            scmNretailTimeMapper.truncate();
        }

    }

    @Override
    public List<ScmNretailTime> getAll(String database) {
        if(database.equals("mysql")){
            return scmNretailTimeMapper.getAll();
        }
        return null;
    }
}
