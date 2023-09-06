package com.application.service;

import com.application.constant.SchemaConstant;
import com.application.dto.Sql.TopSellProduct;
import com.application.repository.OrderRepo;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class StatisticalService {
//    @Autowired
//    private EntityManager entityManager;
//    public Object getTopSell(Integer top, LocalDateTime from,LocalDateTime to) throws SQLException {
//        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(SchemaConstant.ANIME_SHOP_SCHEMA + "."+SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE);
//        query.registerStoredProcedureParameter("pTop",Integer.class, ParameterMode.IN);
//        query.registerStoredProcedureParameter("pFrom",LocalDateTime.class, ParameterMode.IN);
//        query.registerStoredProcedureParameter("pTo",LocalDateTime.class, ParameterMode.IN);
//        // set data
//        query.setParameter("pTop",top);
//        query.setParameter("pFrom",from);
//        query.setParameter("pTo",to);
//        // i want get data returned by procedure
//
//        return null;
//    }
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private OrderRepo orderRepo;
    public List getTopSell(Integer top, Long from,Long to) throws SQLException {
        // set data
//        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate);
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource);
        simpleJdbcCall.withProcedureName(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.name);
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.INPUT_TOP,top)
                .addValue(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.INPUT_FROM,buildStart(from))
                .addValue(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.INPUT_TO,buildEnd(to));
        Map<String,Object> mapValue = simpleJdbcCall.execute(source);
        System.out.println(mapValue);
        List<Map<String,Object>> resultSet = (List<Map<String, Object>>) mapValue.get("#result-set-1");
        List topSellProductData = new LinkedList();
        for (Map<String,Object> item: resultSet) {
            Integer id = Integer.valueOf(item.get(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.OUTPUT_ID)+"");
            Integer quantity = Integer.valueOf(item.get(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.OUTPUT_QUANTITY)+"");
            String name = String.valueOf(item.get(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.OUTPUT_NAME));
            TopSellProduct topSellProduct = new TopSellProduct(id,name,quantity);
            topSellProductData.add(topSellProduct);
//            System.out.println(item.get(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.OUTPUT_ID)+""+item.get(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.OUTPUT_NAME)
//                    +""+item.get(SchemaConstant.GET_TOP_SELL_PRODUCT_PROCEDURE.OUTPUT_QUANTITY));
        }

        // i want get data returned by procedure

        return topSellProductData;
    }
    public Object getProfit(Integer year){
        LocalDateTime from = LocalDateTime.of(year,1,1,0,0,0);
        LocalDateTime to = LocalDateTime.of(year+1,1,1,0,0,0);
        Date fromDate = Date.from(from.atZone(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(to.atZone(ZoneId.systemDefault()).toInstant());
//        java.sql.Date fromDate = java.sql.Date.valueOf(from.toLocalDate());
//        java.sql.Date toDate = java.sql.Date.valueOf(to.toLocalDate());

        List map = orderRepo.statistical(fromDate,toDate);
        System.out.println("s");
        return map;
    }
    private LocalDateTime buildStart(Long date){
        if(date == null){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        LocalDateTime time = LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)+1,
                calendar.get((Calendar.DAY_OF_MONTH)),
                0,0
        );
        return time;
    }
    private LocalDateTime buildEnd(Long date){
        if(date == null){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        LocalDateTime time = LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)+1,
                calendar.get((Calendar.DAY_OF_MONTH)),
                23,59,59
        );
        return time;
    }
}
