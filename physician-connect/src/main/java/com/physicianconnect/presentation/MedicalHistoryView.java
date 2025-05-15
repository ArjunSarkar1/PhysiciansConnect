package com.physicianconnect.presentation;

import com.physicianconnect.objects.MedicalHistory;
import java.util.List;

public interface MedicalHistoryView extends Viewable {
    void setHistoryEntries(List<MedicalHistory> entries);
}
