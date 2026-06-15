package org.example.myblogbackend.service;

import org.example.myblogbackend.dto.CategoryForm;
import org.example.myblogbackend.dto.CategoryVO;
import org.example.myblogbackend.dto.IdVO;

import java.util.List;

public interface CategoryService {

    List<CategoryVO> list();

    IdVO create(CategoryForm form);

    void update(Long id, CategoryForm form);

    void delete(Long id);
}
