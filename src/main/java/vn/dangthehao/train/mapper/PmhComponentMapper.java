package vn.dangthehao.train.mapper;

import org.mapstruct.*;
import vn.dangthehao.train.dto.component.request.CreatePmhComponentRequest;
import vn.dangthehao.train.dto.component.request.UpdatePmhComponentRequest;
import vn.dangthehao.train.dto.component.response.FullDetailComponentResponse;
import vn.dangthehao.train.dto.component.response.PmhComponentResponse;
import vn.dangthehao.train.dto.component.response.DetailPmhComponentResponse;
import vn.dangthehao.train.dto.component.NewDataComponent;
import vn.dangthehao.train.entity.PmhComponents1;

@Mapper(componentModel = "spring")
public interface PmhComponentMapper {
  PmhComponents1 toComponentEntity(CreatePmhComponentRequest request);

  @Mapping(target = "status", ignore = true)
  @Mapping(target = "messageType", ignore = true)
  DetailPmhComponentResponse toDetailComponentResponse(PmhComponents1 entity);

  NewDataComponent toNewDataComponent(PmhComponents1 entity);

  PmhComponentResponse toComponentResponse(PmhComponents1 entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "isDisplay", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "status", ignore = true)
  void updateComponentFromRequest(
      UpdatePmhComponentRequest request, @MappingTarget PmhComponents1 entity);

  @Mapping(target = "status", ignore = true)
  @Mapping(target = "messageType", ignore = true)
  FullDetailComponentResponse toFullDetailComponentResponse(PmhComponents1 entity);
}
