package org.example.myblogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.BusinessException;
import org.example.myblogbackend.dto.CategoryForm;
import org.example.myblogbackend.dto.CategoryVO;
import org.example.myblogbackend.dto.CountResult;
import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.entity.Category;
import org.example.myblogbackend.mapper.ArticleMapper;
import org.example.myblogbackend.mapper.CategoryMapper;
import org.example.myblogbackend.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    @Override
    public List<CategoryVO> list() {
        List<Category> categories = categoryMapper.selectList(
                new QueryWrapper<Category>().orderByAsc("id"));
        Map<Long, Long> counts = articleMapper.countPublishedByCategory().stream()
                .collect(Collectors.toMap(CountResult::getId, CountResult::getCount));
        return categories.stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            vo.setId(c.getId());
            vo.setName(c.getName());
            vo.setArticleCount(counts.getOrDefault(c.getId(), 0L));
            return vo;
        }).toList();
    }

    @Override
    public IdVO create(CategoryForm form) {
        ensureNameUnique(form.getName(), null);
        Category c = new Category();
        c.setName(form.getName());
        categoryMapper.insert(c);
        return new IdVO(c.getId());
    }

    @Override
    public void update(Long id, CategoryForm form) {
        Category c = requireById(id);
        ensureNameUnique(form.getName(), id);
        c.setName(form.getName());
        categoryMapper.updateById(c);
    }

    @Override
    public void delete(Long id) {
        requireById(id);
        categoryMapper.deleteById(id);
    }

    private Category requireById(Long id) {
        Category c = categoryMapper.selectById(id);
        if (c == null) {
            throw new BusinessException("分类不存在");
        }
        return c;
    }

    private void ensureNameUnique(String name, Long excludeId) {
        QueryWrapper<Category> qw = new QueryWrapper<Category>().eq("name", name);
        if (excludeId != null) {
            qw.ne("id", excludeId);
        }
        if (categoryMapper.selectCount(qw) > 0) {
            throw new BusinessException("分类名已存在");
        }
    }
}
