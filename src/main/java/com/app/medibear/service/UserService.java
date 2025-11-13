package com.app.medibear.service;

import com.app.medibear.dto.PasswordChangeRequest;
import com.app.medibear.dto.SignUpRequest;
import com.app.medibear.model.User;
import com.app.medibear.repository.SleepDataRepository;
import com.app.medibear.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SleepDataRepository sleepDataRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            SleepDataRepository sleepDataRepository
    ) {
        this.userRepository = userRepository;
        this.sleepDataRepository = sleepDataRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /** 회원 가입 */
    public User createUser(SignUpRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (req.getPassword() == null || req.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setGender(req.getGender());
        user.setName(req.getName());
        user.setBirthDate(LocalDate.parse(req.getBirthDate()));

        return userRepository.save(user);
    }

    /** email로 회원 조회 */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    /** memberNo(PK)로 회원 조회 */
    public User getUserByMemberNo(Long memberNo) {
        return userRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + memberNo));
    }

    /** email 기반 → memberNo(PK) 변환 */
    public Long getMemberNoByEmail(String email) {
        return getUserByEmail(email).getMemberNo();
    }

    /** 비밀번호 수정 */
    public void changePassword(PasswordChangeRequest req) {

        // email 기반 조회
        User user = getUserByEmail(req.getEmail());

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        if (req.getNewPassword() == null || req.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("새 비밀번호는 8자 이상이어야 합니다.");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    /** 회원 탈퇴 — email 기반 */
    @Transactional
    public void deleteUser(String email) {

        User user = getUserByEmail(email);
        Long memberNo = user.getMemberNo(); // SleepData 삭제 위해 필요함

        // 1. 수면 데이터 삭제 (member_no FK)
        sleepDataRepository.deleteByMemberNo(memberNo);

        // 2. 회원 삭제
        userRepository.delete(user);
    }

    /** 나이 계산 */
    public int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /** gender enum → int */
    public int toGenderInt(User.Gender genderEnum) {
        if (genderEnum == null) return 0;
        return (genderEnum == User.Gender.M) ? 0 : 1;
    }
}
