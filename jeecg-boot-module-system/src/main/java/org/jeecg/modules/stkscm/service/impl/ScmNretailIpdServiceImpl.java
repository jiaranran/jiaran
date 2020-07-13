package org.jeecg.modules.stkscm.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jeecg.modules.stkjob.entity.ScmNretailJobStk;
import org.jeecg.modules.stkjob.mapper.ScmNretailJobStkMapper;
import org.jeecg.modules.stkscm.mapper.ScmNretailIpdMapper;
import org.jeecg.modules.stkscm.service.ScmNretailIpdService;
import org.jeecg.modules.stkscm.vo.ScmNretailpiVo;
import org.jeecg.modules.stkpredict.entity.ScmNretailIpd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: 销量统计按天
 * @Author: jeecg-boot
 * @Date:   2020-03-11
 * @Version: V1.0
 */
@Service
@DS("input")
public class ScmNretailIpdServiceImpl extends ServiceImpl<ScmNretailIpdMapper, ScmNretailIpd> implements ScmNretailIpdService {
    @Autowired
    ScmNretailIpdMapper scmNretailIpdMapper;
    @Autowired
    ScmNretailJobStkMapper scmNretailJobStkMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void truncate( String database){
        if(database.equals("mysql")){
            scmNretailIpdMapper.truncate();
        }
    }

    public boolean saveIpds(List<ScmNretailIpd> lists, String database){
        try(SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);) {
            //由于数据库对于插入字段的限制，在这里对批量插入的数据进行分批处理
            long start = System.currentTimeMillis();
            for (int i = 0; i <= lists.size() - 1;i++) {
                scmNretailIpdMapper.saveOne(lists.get(i));
                if(i%10000==9999){
                    sqlSession.commit();
                    sqlSession.clearCache();
                }
            }
            sqlSession.commit();
            sqlSession.clearCache();
            System.out.println("批处理执行时间"+(System.currentTimeMillis()-start)/1000+"秒");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Transactional
    @Override
    public boolean  save(String uuid,List<ScmNretailIpd> scmNretailIpdList, String database){
        ScmNretailJobStk job = new ScmNretailJobStk();
        try {
            truncate(database);
            return saveIpds(scmNretailIpdList,database);
        }catch (Exception e){
            job.setUuid(uuid);
            job.setState("fail");
            job.setJobinfo("汇总数据入库失败");
            job.setEndtime(new Date());
            scmNretailJobStkMapper.update(job);
            return false;
        }

    }
    @Override
    public List<ScmNretailIpd>  getAll(String database){
        return scmNretailIpdMapper.getAll();
    }
    @Override
    public List<ScmNretailpiVo> countByGpw(String database) {
        return scmNretailIpdMapper.countByGpw();
    }

    @Override
    public void saveOne(ScmNretailIpd scmNretailIpw) {
        scmNretailIpdMapper.saveOne(scmNretailIpw);
    }



}
