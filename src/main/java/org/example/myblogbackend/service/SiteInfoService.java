package org.example.myblogbackend.service;

import org.example.myblogbackend.dto.SiteInfoForm;
import org.example.myblogbackend.dto.SiteInfoVO;
import org.example.myblogbackend.entity.SiteInfo;

public interface SiteInfoService {

    /** 公开:左栏 + 关于页(含统计数)。 */
    SiteInfoVO getInfo();

    /** 管理:编辑回填(实体原始字段)。 */
    SiteInfo getForAdmin();

    void update(SiteInfoForm form);
}
