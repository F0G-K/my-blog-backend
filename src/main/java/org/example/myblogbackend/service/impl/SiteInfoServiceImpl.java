package org.example.myblogbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.dto.SiteInfoForm;
import org.example.myblogbackend.dto.SiteInfoVO;
import org.example.myblogbackend.entity.SiteInfo;
import org.example.myblogbackend.mapper.ArticleMapper;
import org.example.myblogbackend.mapper.CategoryMapper;
import org.example.myblogbackend.mapper.SiteInfoMapper;
import org.example.myblogbackend.mapper.TagMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.myblogbackend.entity.Article;
import org.example.myblogbackend.service.SiteInfoService;

@Service
@RequiredArgsConstructor
public class SiteInfoServiceImpl implements SiteInfoService {

    private static final long SITE_ID = 1L;

    private final SiteInfoMapper siteInfoMapper;
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    @Override
    public SiteInfoVO getInfo() {
        SiteInfo si = getOrEmpty();
        SiteInfoVO vo = new SiteInfoVO();
        vo.setNickname(si.getNickname());
        vo.setAvatarUrl(si.getAvatarUrl());
        vo.setBio(si.getBio());
        vo.setGithub(si.getGithubUrl());
        vo.setWechat(si.getWechat());
        vo.setRss(si.getRssUrl());
        vo.setEmail(si.getEmail());
        vo.setLocation(si.getLocation());
        vo.setJob(si.getJob());
        vo.setWechatQrUrl(si.getWechatQrUrl());
        vo.setAboutContent(si.getAboutContent());
        vo.setArticleCount(articleMapper.selectCount(new QueryWrapper<Article>().eq("status", 1)));
        vo.setCategoryCount(categoryMapper.selectCount(null));
        vo.setTagCount(tagMapper.selectCount(null));
        return vo;
    }

    @Override
    public SiteInfo getForAdmin() {
        return getOrEmpty();
    }

    @Override
    public void update(SiteInfoForm form) {
        SiteInfo si = siteInfoMapper.selectById(SITE_ID);
        boolean isNew = si == null;
        if (isNew) {
            si = new SiteInfo();
            si.setId(SITE_ID);
        }
        // updateById 默认忽略 null 字段;新增时一并写入
        si.setNickname(form.getNickname());
        si.setAvatarUrl(form.getAvatarUrl());
        si.setBio(form.getBio());
        si.setAboutContent(form.getAboutContent());
        si.setEmail(form.getEmail());
        si.setWechat(form.getWechat());
        si.setWechatQrUrl(form.getWechatQrUrl());
        si.setGithubUrl(form.getGithubUrl());
        si.setRssUrl(form.getRssUrl());
        si.setLocation(form.getLocation());
        si.setJob(form.getJob());
        if (isNew) {
            siteInfoMapper.insert(si);
        } else {
            siteInfoMapper.updateById(si);
        }
    }

    private SiteInfo getOrEmpty() {
        SiteInfo si = siteInfoMapper.selectById(SITE_ID);
        return si == null ? new SiteInfo() : si;
    }
}
