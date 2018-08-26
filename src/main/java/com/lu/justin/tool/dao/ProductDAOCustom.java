package com.lu.justin.tool.dao;

import com.lu.justin.tool.dao.dto.ProductDTO;

import java.util.List;

public interface ProductDAOCustom {
    List<ProductDTO> selectBySearcher(List<String> prdIds);
}
