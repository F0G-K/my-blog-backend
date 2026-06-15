package org.example.myblogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.BusinessException;
import org.example.myblogbackend.dto.CountResult;
import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.dto.TagForm;
import org.example.myblogbackend.dto.TagVO;
import org.example.myblogbackend.entity.Tag;
import org.example.myblogbackend.mapper.ArticleTagMapper;
import org.example.myblogbackend.mapper.TagMapper;
import org.example.myblogbackend.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    @Override
    public List<TagVO> list() {
        List<Tag> tags = tagMapper.selectList(new QueryWrapper<Tag>().orderByAsc("id"));
        Map<Long, Long> counts = articleTagMapper.countPublishedByTag().stream()
                .collect(Collectors.toMap(CountResult::getId, CountResult::getCount));
        return tags.stream().map(t -> {
            TagVO vo = new TagVO();
            vo.setId(t.getId());
            vo.setName(t.getName());
            vo.setArticleCount(counts.getOrDefault(t.getId(), 0L));
            return vo;
        }).toList();
    }

    @Override
    public IdVO create(TagForm form) {
        if (tagMapper.selectCount(new QueryWrapper<Tag>().eq("name", form.getName())) > 0) {
            throw new BusinessException("标签名已存在");
        }
        Tag t = new Tag();
        t.setName(form.getName());
        tagMapper.insert(t);
        return new IdVO(t.getId());
    }

    @Override
    public void delete(Long id) {
        if (tagMapper.selectById(id) == null) {
            throw new BusinessException("标签不存在");
        }
        tagMapper.deleteById(id);
        // 同时清理文章-标签关联
        articleTagMapper.delete(new QueryWrapper<org.example.myblogbackend.entity.ArticleTag>().eq("tag_id", id));
    }
}
