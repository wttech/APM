package com.cognifide.apm.core.actions;

import com.cognifide.apm.core.Property;
import com.cognifide.apm.core.crypto.DecryptionService;
import lombok.Getter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = MapperContext.class,
    property = {
        Property.DESCRIPTION + "Mapper Context service",
        Property.VENDOR
    }
)
@Getter
public class MapperContext {

  @Reference
  private DecryptionService decryptionService;

}
