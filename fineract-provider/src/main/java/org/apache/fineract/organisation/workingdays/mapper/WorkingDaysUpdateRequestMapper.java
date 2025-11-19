package org.apache.fineract.organisation.workingdays.mapper;

import org.apache.fineract.infrastructure.core.config.MapstructMapperConfig;
import org.apache.fineract.organisation.workingdays.data.WorkingDaysData;
import org.apache.fineract.organisation.workingdays.data.WorkingDaysUpdateRequest;
import org.apache.fineract.organisation.workingdays.data.WorkingDaysUpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapstructMapperConfig.class)
public interface WorkingDaysUpdateRequestMapper {

    @Mapping(target = "description", expression = "java(org.apache.fineract.infrastructure.businessdate.domain.BusinessDateType.valueOf(source.getType()).getDescription())")
    @Mapping(target = "type", expression = "java(org.apache.fineract.infrastructure.businessdate.domain.BusinessDateType.valueOf(source.getType()))")
    @Mapping(target = "date", expression = "java(org.apache.fineract.infrastructure.core.service.DateUtils.toLocalDate(source.getLocale(), source.getDate(), source.getDateFormat()))")
    @Mapping(target = "changes", ignore = true)
    @Mapping(target = "recurrence", expression = "java(org.apache.fineract.organization.workingdays.WorkingDaysData.valueOf(source.getType()).getRecurrence") //TODO change the type?
    @Mapping(target = "repaymentRescheduleType", source = "source.repaymentRescheduleType")
    @Mapping(target = "extendTermForDailyRepayments", source = "source.extendTermForDailyRepayments")
    @Mapping(target = "extendTermForRepaymentsOnHolidays", source = "source.extendTermForRepaymentsOnHolidays")
    WorkingDaysData mapUpdateRequest(WorkingDaysUpdateRequest source);

    WorkingDaysUpdateResponse mapFetchResponse(WorkingDaysData source);

}
