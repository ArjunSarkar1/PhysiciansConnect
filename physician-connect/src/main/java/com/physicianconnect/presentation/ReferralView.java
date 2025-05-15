package com.physicianconnect.presentation;

import com.physicianconnect.objects.Referral;
import java.util.List;

public interface ReferralView extends Viewable {
    void setReferrals(List<Referral> referrals);
}
