package org.apache.fineract.organisation.workingdays.data;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingDaysUpdateRequest implements Serializable {

    @Serial
    public static final long serialVersionUID = 1L;
    @NotNull(message = "{workingdays.recurrence.mandatory}")
    private String recurrence;

    @NotNull(message = "{workingdays.repaymentRescheduleType.mandatory}")
    @Min(value = 1, message = "{workingdays.repaymentRescheduleType.min}")
    @Max(value = 4, message = "{workingdays.repaymentRescheduleType.max}")
    private Integer repaymentRescheduleType;

    @NotNull(message = "{workingdays.extendTermForDailyRepayments.mandatory}")
    private Boolean extendTermForDailyRepayments;

    @NotNull(message = "{workingdays.extendTermForRepaymentsOnHolidays.mandatory}")
    private Boolean extendTermForRepaymentsOnHolidays;

}
