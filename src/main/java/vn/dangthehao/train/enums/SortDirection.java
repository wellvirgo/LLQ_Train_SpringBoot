package vn.dangthehao.train.enums;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SortDirection {
    ASC("asc"),
    DESC("desc")
    ;

    String value;

    SortDirection(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
