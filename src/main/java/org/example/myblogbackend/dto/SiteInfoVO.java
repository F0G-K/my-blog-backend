package org.example.myblogbackend.dto;

import lombok.Data;

/** 站点信息(左栏 + 关于页)。统计数由后端 COUNT 计算。 */
@Data
public class SiteInfoVO {

    private String nickname;

    private String avatarUrl;

    private String bio;

    /** 社交链接(= 实体 githubUrl)。 */
    private String github;

    private String wechat;

    /** = 实体 rssUrl。 */
    private String rss;

    private long articleCount;

    private long tagCount;

    private long categoryCount;

    private String email;

    private String location;

    private String job;

    private String wechatQrUrl;

    private String aboutContent;
}
