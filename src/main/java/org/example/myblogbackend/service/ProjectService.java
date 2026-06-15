package org.example.myblogbackend.service;

import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.dto.ProjectForm;
import org.example.myblogbackend.dto.ProjectVO;

import java.util.List;

public interface ProjectService {

    List<ProjectVO> list();

    IdVO create(ProjectForm form);

    void update(Long id, ProjectForm form);

    void delete(Long id);
}
