package ge.tbc.testautomation.database.mappers;

import ge.tbc.testautomation.database.models.CurrencyConversion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CurrencyConversionMapper {

    List<CurrencyConversion> findActive();

    CurrencyConversion findById(@Param("id") int id);

    int countActive();
}
