package privatebank.demo.model;

import lombok.Getter;

@Getter
public enum Currency {
    UAH(980), USD(840), EUR(978);

    private final Integer code;

    Currency(Integer code) {
        this.code = code;
    }
}
