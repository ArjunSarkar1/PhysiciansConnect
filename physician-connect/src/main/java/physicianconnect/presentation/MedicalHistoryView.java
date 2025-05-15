package physicianconnect.presentation;

import java.util.List;

import physicianconnect.objects.MedicalHistory;

public interface MedicalHistoryView extends Viewable {
    void setHistoryEntries(List<MedicalHistory> entries);
}
