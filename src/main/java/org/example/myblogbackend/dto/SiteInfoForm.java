package org.example.myblogbackend.dto;

import lombok.Data;

/** 站点信息 / 作者资料更新(全部可选)。 */
@Data
public class SiteInfoForm {

    private String nickname;

    private String avatarUrl;

    private String bio;

    private String aboutContent;

    private String email;

    private String wechat;

    private String wechatQrUrl;

    private String githubUrl;

    private String rssUrl;

    private String location;

    private String job;
}
