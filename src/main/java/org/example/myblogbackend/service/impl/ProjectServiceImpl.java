package org.example.myblogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.BusinessException;
import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.dto.ProjectForm;
import org.example.myblogbackend.dto.ProjectVO;
import org.example.myblogbackend.entity.Project;
import org.example.myblogbackend.mapper.ProjectMapper;
import org.example.myblogbackend.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    @Override
    public List<ProjectVO> list() {
        List<Project> projects = projectMapper.selectList(
                new QueryWrapper<Project>().orderByAsc("sort").orderByAsc("id"));
        return projects.stream().map(this::toVO).toList();
    }

    @Override
    public IdVO create(ProjectForm form) {
        Project p = new Project();
        apply(p, form);
        projectMapper.insert(p);
        return new IdVO(p.getId());
    }

    @Override
    public void update(Long id, ProjectForm form) {
        Project p = projectMapper.selectById(id);
        if (p == null) {
            throw new BusinessException("项目不存在");
        }
        apply(p, form);
        projectMapper.updateById(p);
    }

    @Override
    public void delete(Long id) {
        if (projectMapper.selectById(id) == null) {
            throw new BusinessException("项目不存在");
        }
        projectMapper.deleteById(id);
    }

    private void apply(Project p, ProjectForm form) {
        p.setName(form.getName());
        p.setDescription(form.getDescription());
        p.setTechStack(form.getTechStack());
        p.setGithubUrl(form.getGithubUrl());
        p.setSort(form.getSort() == null ? 0 : form.getSort());
    }

    private ProjectVO toVO(Project p) {
        ProjectVO vo = new ProjectVO();
        vo.setId(p.getId());
        vo.setName(p.getName());
        vo.setDescription(p.getDescription());
        vo.setTechStack(p.getTechStack());
        vo.setGithubUrl(p.getGithubUrl());
        return vo;
    }
}
