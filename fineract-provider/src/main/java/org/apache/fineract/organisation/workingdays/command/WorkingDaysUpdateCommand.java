package org.apache.fineract.organisation.workingdays.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.fineract.command.core.Command;

import org.apache.fineract.organisation.workingdays.data.WorkingDaysUpdateRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkingDaysUpdateCommand extends Command<WorkingDaysUpdateRequest>{}
