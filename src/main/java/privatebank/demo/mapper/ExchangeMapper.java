package privatebank.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import privatebank.demo.dto.ExchangeRateDto;
import privatebank.demo.model.ExchangeRate;

@Mapper(componentModel = "spring")
public interface ExchangeMapper {

    @Mapping(source = "averageBuyRate", target = "rateBuy")
    @Mapping(source = "averageSellRate", target = "rateSell")
    @Mapping(source = "createdAt", target = "date")
    ExchangeRateDto toDto(ExchangeRate exchangeRate);

}
