package oneapp.workbox.poadapter.services.services;

import javax.ejb.Local;

import com.incture.pmc.poadapter.dto.CustomAttributeDto;

@Local
public interface CustomAttributesServiceLocal {

	CustomAttributeDto getCustomAttributes(String taskId);

}
