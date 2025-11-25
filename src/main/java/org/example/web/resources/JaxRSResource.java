package org.example.web.resources;

import java.util.List;

public interface JaxRSResource {

    default String getResourceName() {
        return this.getClass().getSimpleName();
    }

    List<String> paths();
}
