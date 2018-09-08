package com.lu.justin.tool.dao;

import com.lu.justin.tool.dao.custom.CusProductDAO;
import com.lu.justin.tool.dao.dto.ProductDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInfoDAO extends MongoRepository<ProductDTO, Long>, CusProductDAO {

    @Query("{'prdId':{'$in':?0}}")
    List<ProductDTO> selectBySearcher(List<String> prdIds);
}
