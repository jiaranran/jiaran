package org.jeecg.modules.result.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ScmNretailResultVo {
    private String matnr;
    private String prov;
    private Date ds;
    private Double cnt;
    private Double pred;
    private Double process_pred;
    private String eval;
    private String process_eval;
}
