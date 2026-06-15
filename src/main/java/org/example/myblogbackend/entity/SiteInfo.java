package org.example.myblogbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 站点信息 / 作者资料(单行,固定 id=1)。 */
@Data
@TableName("site_info")
public class SiteInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

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

    private LocalDateTime updatedAt;
}
