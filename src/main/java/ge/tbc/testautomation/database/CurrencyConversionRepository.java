package ge.tbc.testautomation.database;

import ge.tbc.testautomation.database.mappers.CurrencyConversionMapper;
import ge.tbc.testautomation.database.models.CurrencyConversion;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class CurrencyConversionRepository {

    public List<CurrencyConversion> findActive() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(CurrencyConversionMapper.class).findActive();
        }
    }

    public int countActive() {
        try (SqlSession session = MyBatisSessionFactory.openSession()) {
            return session.getMapper(CurrencyConversionMapper.class).countActive();
        }
    }
}
