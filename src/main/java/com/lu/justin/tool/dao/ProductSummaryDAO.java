package com.lu.justin.tool.dao;

import com.lu.justin.tool.dao.dto.ProductSummaryDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ProductSummaryDAO extends MongoRepository<ProductSummaryDTO, Long> {

    ProductSummaryDTO findByDate(Date date);

}
