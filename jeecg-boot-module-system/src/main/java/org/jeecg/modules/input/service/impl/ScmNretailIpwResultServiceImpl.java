package org.jeecg.modules.input.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.jeecg.modules.input.entity.ScmNretailIpwResult;
import org.jeecg.modules.input.mapper.ScmNretailIpwResultMapper;
import org.jeecg.modules.input.service.IScmNretailIpwResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 输入文件预测结果
 * @Author: jeecg-boot
 * @Date:   2020-06-11
 * @Version: V1.0
 */
@Service
@DS("predict")
public class ScmNretailIpwResultServiceImpl extends ServiceImpl<ScmNretailIpwResultMapper, ScmNretailIpwResult> implements IScmNretailIpwResultService {
    @Autowired
    ScmNretailIpwResultMapper scmNretailIpwResultMapper;
    public void truncate() {
        scmNretailIpwResultMapper.truncate();
    }
}
