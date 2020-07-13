package org.jeecg.modules.stkpredict.service.impl;
import org.jeecg.modules.stkjob.mapper.ScmNretailJobStkMapper;
import org.jeecg.modules.stkpredict.entity.ScmNretailIpd;
import org.jeecg.modules.stkpredict.entity.ScmNretailStk;
import org.jeecg.modules.stkpredict.mapper.ScmNretailStkMapper;
import org.jeecg.modules.stkpredict.service.IScmNretailStkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.jeecg.modules.util.DateUtils.*;
import static org.jeecg.modules.util.DateUtils.getNextDate;

/**
 * @Description: 原始数据
 * @Author: jeecg-boot
 * @Date:   2020-07-06
 * @Version: V1.0
 */
@Service
public class ScmNretailStkServiceImpl extends ServiceImpl<ScmNretailStkMapper, ScmNretailStk> implements IScmNretailStkService {
    @Autowired
    ScmNretailStkMapper scmNretailStkMapper;

    @Override
    public void truncate(String database) {
        if(database.equals("mysql")){
            scmNretailStkMapper.truncate();
        }

    }

    @Transactional
    @Override
    public void save(List<ScmNretailStk> listscmNretails, String database) {
        if(database.equals("mysql")){
            super.saveBatch(listscmNretails);
        }

    }

    @Override
    public List<ScmNretailIpd> countByGpw(String database) {
        return scmNretailStkMapper.countByGpw();
    }

    @Override
    public List<ScmNretailIpd> dsList2(List<ScmNretailIpd> scmNretailIpdList, String database,String fileName){
//        preDate最后一天日期
        Date preDate  = getDate(fileName);
        int i=0;
        ScmNretailIpd scmNretailIpw = scmNretailIpdList.get(i);
        String prov = scmNretailIpw.getProv();
        String matnr = scmNretailIpw.getMatnr();
        ArrayList<ScmNretailIpd> allList = new ArrayList<ScmNretailIpd>();
        ArrayList<ScmNretailIpd> list = new ArrayList<ScmNretailIpd>();
        list.add(scmNretailIpdList.get(i));
        Date nextDate = getNextDate(scmNretailIpw.getDs());
        for (Date date = scmNretailIpw.getDs(); i<scmNretailIpdList.size()-1; nextDate=getNextDate(nextDate)) {
            ++i;
            scmNretailIpw = scmNretailIpdList.get(i);
            if(prov.equals(scmNretailIpw.getProv())&&matnr.equals(scmNretailIpw.getMatnr())){
//                如果nextWeek 18和下一个 scmNretailIpw 1日期不同
//                nextWeek增加 scmNretailIpw不变 直到相等
                if(!nextDate.equals(scmNretailIpw.getDs())){
                    ScmNretailIpd ipw = new ScmNretailIpd();
                    ipw.setProv(prov);
                    ipw.setMatnr(matnr);
                    ipw.setDs(nextDate);
                    ipw.setCnt((double) 0);
                    list.add(ipw);
                    i--;
                }else {
                    list.add(scmNretailIpw);
                }
                if(i==scmNretailIpdList.size()-1){
                    for (nextDate=getNextDate(nextDate); nextDate.before(preDate)||nextDate.equals(preDate) ; nextDate=getNextDate(nextDate)) {
                        ScmNretailIpd ipw = new ScmNretailIpd();
                        ipw.setProv(prov);
                        ipw.setMatnr(matnr);
                        ipw.setDs(nextDate);
                        ipw.setCnt((double) 0);
                        list.add(ipw);
                    }
                }
            }else {
                for (int j=0; nextDate.before(preDate)||nextDate.equals(preDate) ; nextDate=getNextDate(nextDate)) {
                    ScmNretailIpd ipw = new ScmNretailIpd();
                    ipw.setProv(prov);
                    ipw.setMatnr(matnr);
                    ipw.setDs(nextDate);
                    ipw.setCnt((double) 0);
                    list.add(ipw);
                }
                allList.addAll(list);
                list=new ArrayList<ScmNretailIpd>();
                prov = scmNretailIpw.getProv();
                matnr = scmNretailIpw.getMatnr();
                date = scmNretailIpw.getDs();
                list.add(scmNretailIpw);
                nextDate = scmNretailIpw.getDs();
                continue;
            }
        }
        allList.addAll(list);
        return allList;
    }

}
