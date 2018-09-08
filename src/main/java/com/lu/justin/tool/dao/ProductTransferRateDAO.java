package com.lu.justin.tool.dao;

import com.lu.justin.tool.dao.dto.ProductTransferRateDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;

@Repository
public interface ProductTransferRateDAO extends MongoRepository<ProductTransferRateDTO, Long> {

    ProductTransferRateDTO findByDate(LocalDate date);
}
