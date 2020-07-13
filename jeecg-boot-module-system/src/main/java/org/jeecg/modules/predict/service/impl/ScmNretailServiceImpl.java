package org.jeecg.modules.predict.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.job.mapper.ScmNretailJobMapper;
import org.jeecg.modules.job.service.ScmNretailJobService;
import org.jeecg.modules.predict.entity.ScmNretail;
import org.jeecg.modules.predict.entity.ScmNretailIpw;
import org.jeecg.modules.predict.mapper.ScmNretailMapper;
import org.jeecg.modules.predict.service.ScmNretailService;
import org.jeecg.modules.predict.vo.ScmNretailDsVo;
import org.jeecg.modules.predict.vo.ScmNretailIpdVo;
import org.jeecg.modules.result.service.IScmNretailResultService;
import org.jeecg.modules.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.jeecg.modules.util.DateUtils.*;

/**
 * @Description: 原始数据
 * @Author: jeecg-boot
 * @Date:   2020-03-14
 * @Version: V1.0
 */

@Service
@Slf4j
public class ScmNretailServiceImpl extends ServiceImpl<ScmNretailMapper, ScmNretail> implements ScmNretailService {
//    @Autowired
//    @Qualifier(value = "primaryMongoTemplate")
    private MongoTemplate primaryMongoTemplate;
    @Autowired
    private IScmNretailResultService scmNretailResultService;
    @Autowired
    private ScmNretailJobService scmNretailJobService;

    @Autowired
    ScmNretailMapper scmNretailMapper;
    @Autowired
    ScmNretailJobMapper scmNretailJobMapper;
    /**
     * 保存对象
     * @param
     * @return
     */
    public void saveMo(List<ScmNretail> listscmNretails) {
        primaryMongoTemplate.insert(listscmNretails,"scm_nretail");
    }

    public void truncateMo() {
//        primaryMongoTemplate.findAllAndRemove(new Query(),"scm_nretail");
        primaryMongoTemplate.dropCollection("scm_nretail");
    }
    public List<ScmNretailIpw> countByGpwMo() {
//        select prov, ds, matnr,SUM(cnt) cnt from scm_nretail group by ds,prov,matnr order by prov, ds, matnr

        Sort sort = new Sort(Sort.Direction.ASC, "matnr").and(new Sort(Sort.Direction.ASC, "prov")).and(new Sort(Sort.Direction.ASC, "ds"));
        Aggregation agg = Aggregation.newAggregation(
                // 第一步：挑选所需的字段，类似select *，*所代表的字段内容
                Aggregation.project("prov", "matnr","ds","cnt"),
                // 第二步：sql where 语句筛选符合条件的记录
                // 第三步：分组条件，设置分组字段
                Aggregation.group("prov", "matnr","ds")
                        .sum("cnt").as("cnt"), // 增加publishDate为分组后输出的字段
                Aggregation.sort(sort),
                // 第四步：重新挑选字段
                Aggregation.project("prov", "matnr", "ds", "cnt")

        );
        AggregationResults<ScmNretailIpw> scmNretailIpws = primaryMongoTemplate.aggregate(agg, "scm_nretail", ScmNretailIpw.class);
        List<ScmNretailIpw> mappedResults = scmNretailIpws.getMappedResults();
        return mappedResults;
    }
    public List<ScmNretailDsVo> getDsMo(){

//        SELECT prov,matnr,max(ds) maxDs ,min(ds) minDs FROM scm_nretail GROUP BY prov, matnr
        Aggregation agg = Aggregation.newAggregation(
                // 第一步：挑选所需的字段，类似select *，*所代表的字段内容
                Aggregation.project("prov", "matnr","ds"),
                // 第二步：sql where 语句筛选符合条件的记录
                // 第三步：分组条件，设置分组字段
                Aggregation.group("prov", "matnr")
                        .max("ds").as("maxDs").min("ds").as("minDs"), // 增加publishDate为分组后输出的字段
                // 第四步：重新挑选字段
                Aggregation.project("prov", "matnr", "maxDs", "minDs")
        );
        AggregationResults<ScmNretailDsVo> results = primaryMongoTemplate.aggregate(agg, "scm_nretail", ScmNretailDsVo.class);
        List<ScmNretailDsVo> ds = results.getMappedResults();
        return ds;
    }
    public List<Date> findDsListMo(ScmNretailDsVo vo) {
        Query query = new Query();
//            SELECT DISTINCT ds  FROM scm_nretail WHERE prov=#{prov} and matnr=#{matnr}
        query.addCriteria(Criteria.where("prov").is(vo.getProv()).and("matnr").is(vo.getMatnr()));
        List<Date> dsList = primaryMongoTemplate.findDistinct(query, "ds", "scm_nretail", Date.class);
        return dsList;
    }



    @Override
    public void truncate(String database) {
        if(database.equals("mysql")){
            scmNretailMapper.truncate();
        }else {
            primaryMongoTemplate.dropCollection("scm_nretail");
        }

    }

    @Transactional
    @Override
    public void save(List<ScmNretail> listscmNretails, String database) {
        if(database.equals("mysql")){
            super.saveBatch(listscmNretails);
        }else {
            primaryMongoTemplate.insert(listscmNretails,"scm_nretail");
        }

    }

    @Override
    public void save(List<ScmNretail> listscmNretails, File file) {
//        读取文件的保存
    }

    @Override
    public List<ScmNretailIpdVo> countByIpd() {
        return scmNretailMapper.countByIpd();
    }
    @Override
    public List<ScmNretailIpdVo> countByIpw() {
        return scmNretailMapper.countByIpw();
    }

    @Override
    public List<ScmNretailIpdVo> countByGpd() {
        return scmNretailMapper.countByGpd();
    }

    @Override
    public List<ScmNretailIpw> countByGpw(String database) {
        if(database.equals("mysql")){
            return scmNretailMapper.countByGpw();
        }else {
           return countByGpwMo();
        }

    }
    @Override
    public List<ScmNretailDsVo> naProList(String database){
        List<ScmNretailDsVo> ds=null;
        if(database.equals("mysql")){
            ds = scmNretailMapper.getDs();
        }else {
            ds = getDsMo();
        }
        return ds;
    }

    @Override
    public List<ScmNretailIpw> dsList(List<ScmNretailIpw> scmNretailIpdList, String database){
        ArrayList<ScmNretailIpw> scmNretailIpws = new ArrayList<>();
        scmNretailIpws.addAll(scmNretailIpdList);
        List<ScmNretailDsVo> ds=null;
        List<Date> dsList =null;
        if(database.equals("mysql")){
             ds = scmNretailMapper.getDs();
        }else {
             ds = getDsMo();
        }

        for (ScmNretailDsVo vo :ds) {
//            Date maxDs = vo.getMaxDs();
            Date maxDs = getProWeekDate();
            Date minDs = vo.getMinDs();
            ArrayList<Date> dates = new ArrayList<Date>();
            while(minDs.compareTo(maxDs)==-1){
                minDs= getNextWeek(minDs);
                dates.add(minDs);
            }
            HashMap<String, Object> map = new HashMap();
            map.put("matnr", vo.getMatnr());
            map.put("prov", vo.getProv());
            if(database.equals("mysql")){
                dsList= scmNretailMapper.findDsList(map);
            }else {
                dsList = findDsListMo(vo);
            }

            for (Date date :dates) {
                if(!dsList.contains(date)){
                    ScmNretailIpw scmNretailIpd = new ScmNretailIpw();
                    scmNretailIpd.setProv(vo.getProv());
                    scmNretailIpd.setMatnr(vo.getMatnr());
                    scmNretailIpd.setDs(date);
                    scmNretailIpd.setCnt((double) 0);
                    scmNretailIpws.add(scmNretailIpd);
                }
            }
        }
        return scmNretailIpws;
    }
    @Override
    public List<ScmNretailIpw> dsList2(List<ScmNretailIpw> scmNretailIpdList, String database, String fileName, Integer weekStart){
        if(weekStart==null) weekStart=0;
//        preWeek最后一周日期
        Date preWeek  = getDate(fileName, weekStart);
        int i=0;
        ScmNretailIpw scmNretailIpw = scmNretailIpdList.get(i);
        String prov = scmNretailIpw.getProv();
        String matnr = scmNretailIpw.getMatnr();
        ArrayList<ScmNretailIpw> allList = new ArrayList<ScmNretailIpw>();
        ArrayList<ScmNretailIpw> list = new ArrayList<ScmNretailIpw>();
        list.add(scmNretailIpdList.get(i));
        Date nextWeek = getNextWeek(scmNretailIpw.getDs());
        for (Date date = scmNretailIpw.getDs(); i<scmNretailIpdList.size()-1; nextWeek= DateUtils.getNextWeek(nextWeek)) {
            ++i;
            scmNretailIpw = scmNretailIpdList.get(i);
            if(prov.equals(scmNretailIpw.getProv())&&matnr.equals(scmNretailIpw.getMatnr())){
//                如果nextWeek 18和下一个 scmNretailIpw 1日期不同
//                nextWeek增加 scmNretailIpw不变 直到相等
                if(!nextWeek.equals(scmNretailIpw.getDs())){
                    ScmNretailIpw ipw = new ScmNretailIpw();
                    ipw.setProv(prov);
                    ipw.setMatnr(matnr);
                    ipw.setDs(nextWeek);
                    ipw.setCnt((double) 0);
                    list.add(ipw);
                    i--;
                }else {
                    list.add(scmNretailIpw);
                }
                if(i==scmNretailIpdList.size()-1){
                    for (nextWeek= DateUtils.getNextWeek(nextWeek); nextWeek.before(preWeek)||nextWeek.equals(preWeek) ; nextWeek= DateUtils.getNextWeek(nextWeek)) {
                        ScmNretailIpw ipw = new ScmNretailIpw();
                        ipw.setProv(prov);
                        ipw.setMatnr(matnr);
                        ipw.setDs(nextWeek);
                        ipw.setCnt((double) 0);
                        list.add(ipw);
                    }
                }
            }else {
                for (int j=0; nextWeek.before(preWeek)||nextWeek.equals(preWeek) ; nextWeek= DateUtils.getNextWeek(nextWeek)) {
                    ScmNretailIpw ipw = new ScmNretailIpw();
                    ipw.setProv(prov);
                    ipw.setMatnr(matnr);
                    ipw.setDs(nextWeek);
                    ipw.setCnt((double) 0);
                    list.add(ipw);
                }
                allList.addAll(list);
                list=new ArrayList<ScmNretailIpw>();
                prov = scmNretailIpw.getProv();
                matnr = scmNretailIpw.getMatnr();
                date = scmNretailIpw.getDs();
                list.add(scmNretailIpw);
                nextWeek = scmNretailIpw.getDs();
                continue;
            }
        }
        allList.addAll(list);
        return allList;
    }

}
