//package com.lu.justin.tool.dao.custom;
//
//import com.lu.justin.tool.dao.dto.ProductDTO;
//import com.lu.justin.tool.dao.dto.ProductSummaryDTO;
//import jakarta.annotation.Resource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class CusProductDAOImpl implements CusProductDAO {
//
//    private static final Logger log = LoggerFactory.getLogger(CusProductDAOImpl.class);
//
////    @Resource
////    private MongoOperations mongoTemplate;
//
//    @Override
//    public List<ProductSummaryDTO> groupInfoByDate(LocalDateTime dateTime) {
//
//        LocalDateTime from = dateTime.withSecond(0).withNano(0);
//        LocalDateTime to = from.withSecond(59);
//
////        Query q = Query.query(Criteria.where("validFrom").lte(to).and("validTo").gte(from));
//
//        MatchOperation m = Aggregation.match(Criteria.where("validFrom").lte(to).and("validTo").gte(from));
////        CountOperation co = Aggregation.count().as("count");
//        GroupOperation g = Aggregation.group("category").sum("amount").as("amount").sum("value").as("value").count().as("count");
//
//        log.info("groupInfoByDate condition:{} {}", m, g);
////
//        return mongoTemplate.aggregate(Aggregation.newAggregation(m, g), ProductDTO.class, ProductSummaryDTO.class).getMappedResults();
//    }
//
//    @Override
//    public List<ProductDTO> findByCondition(Query q) {
//        log.info("query list by q:{}", q);
//
//        return mongoTemplate.find(q, ProductDTO.class);
//    }
//}
