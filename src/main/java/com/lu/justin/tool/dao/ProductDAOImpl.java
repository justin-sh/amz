package com.lu.justin.tool.dao;

import com.lu.justin.tool.dao.dto.ProductDTO;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.List;

public class ProductDAOImpl implements ProductDAOCustom {

    @Resource
    private MongoOperations mongoTemplate;

    @Override
    public List<ProductDTO> selectBySearcher(List<String> prdIds) {

        System.out.println("selectBySearcher==" + prdIds);

        Query q = Query.query(Criteria.where("prdId").in(prdIds));
        System.out.println("selectBySearcher==" + q);

        return mongoTemplate.find(q, ProductDTO.class);
    }
}
