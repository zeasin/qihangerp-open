package cn.qihangerp.open.jd.domain.bo;

import lombok.Data;

@Data
public class JdTokenCreateBo {
    private Integer shopId;
    private Integer shopType;
    private String code;
}
