package physicianconnect.presentation;

import java.util.List;

import physicianconnect.objects.Referral;

public interface ReferralView extends Viewable {
    void setReferrals(List<Referral> referrals);
}
