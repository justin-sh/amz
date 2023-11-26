package com.lu.justin.tool.dao;

import com.lu.justin.tool.dao.dto.ProductTransferRateDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ProductTransferRateDAO extends CrudRepository<ProductTransferRateDTO, Long> {

    ProductTransferRateDTO findByDate(LocalDate date);
}
