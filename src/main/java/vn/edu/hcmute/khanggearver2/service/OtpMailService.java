package vn.edu.hcmute.khanggearver2.service;

import vn.edu.hcmute.khanggearver2.domain.OtpPurpose;
import vn.edu.hcmute.khanggearver2.domain.User;

public interface OtpMailService {
    void sendOtp(User user, OtpPurpose purpose, String code);
}
