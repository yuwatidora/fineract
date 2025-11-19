package org.apache.fineract.organisation.workingdays.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingDaysUpdateResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String recurrence;
    private String repaymentRescheduleType;
    private Boolean extendTermForDailyRepayments;
    private Boolean extendTermForRepaymentsOnHolidays;
    private Map<String, Object> changes;
}
