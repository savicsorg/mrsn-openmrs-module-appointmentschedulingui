package org.openmrs.module.appointmentschedulingui.htmlformentry;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;

public class AppointmentCheckInSubmissionAction implements CustomFormSubmissionAction {

    private AppointmentService appointmentService;

    public AppointmentCheckInSubmissionAction(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Override
    public void applyAction(FormEntrySession session) {
        // check in to any scheduled appointments at the specified location on that date
        Date fromDate = new DateTime(session.getSubmissionActions().getCurrentEncounter().getEncounterDatetime()).toDateMidnight().toDate();
        Date toDate = new DateTime(session.getSubmissionActions().getCurrentEncounter().getEncounterDatetime()).withTime(23, 59, 59, 999).toDate();

        // TODO do we need to handle RESCHEDULED here?
        List<Appointment> appointmentList = appointmentService.getAppointmentsByConstraints(fromDate, toDate,
                session.getSubmissionActions().getCurrentEncounter().getLocation(), null, null, Appointment.AppointmentStatus.SCHEDULED, session.getPatient());

        for (Appointment appointment : appointmentList) {
            appointment.setStatus(Appointment.AppointmentStatus.WAITING);
            appointmentService.saveAppointment(appointment);
        }
    }

    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
}