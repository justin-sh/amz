package com.lu.justin.tool.dao;

import com.lu.justin.tool.dao.dto.CurrencyRateDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CurrencyRateDAO extends MongoRepository<CurrencyRateDTO, Long> {

    CurrencyRateDTO findByDateAndFromAndTo(Date date, String from, String to);

}
