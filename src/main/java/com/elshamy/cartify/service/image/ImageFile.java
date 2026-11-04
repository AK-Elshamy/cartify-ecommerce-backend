package com.elshamy.cartify.service.image;

import org.springframework.core.io.Resource;

public record ImageFile(
        Resource resource,
        String contentType
) {
}