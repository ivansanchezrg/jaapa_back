package com.jaapa_back.util;

import com.jaapa_back.dto.response.PageResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

@UtilityClass
public class PageUtils {

    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(page);
    }
}