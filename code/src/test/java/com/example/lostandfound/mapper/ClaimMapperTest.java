package com.example.lostandfound.mapper;

import com.example.lostandfound.domain.entity.Claim;
import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.entity.UserProfile;
import com.example.lostandfound.domain.enums.ClaimStatus;
import com.example.lostandfound.domain.enums.ReportStatus;
import com.example.lostandfound.dto.response.ClaimResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ครอบคลุมฟีเจอร์ "รูปคนขอเคลมข้างชื่อตอนอนุมัติ" (คู่มือทดสอบ กลุ่มที่ 4: TC-08, TC-09)
 * เดิมไม่มีเทสของ ClaimMapper เลย ทั้งที่เป็นจุดที่ generate claimantAvatarUrl / claimantName
 * ให้ ClaimController และ frontend ใช้ตัดสินใจว่าจะโชว์รูปหรือตัวอักษรย่อ
 */
class ClaimMapperTest {

    private final ClaimMapper claimMapper = new ClaimMapper();

    @Test
    void toResponse_shouldIncludeClaimantAvatarUrl_whenProfileHasAvatar() {
        // TC-08: ผู้ขอเคลมมีรูปโปรไฟล์แล้ว -> claimantAvatarUrl ต้องไม่ว่าง
        User claimant = User.builder()
                .id(UUID.randomUUID())
                .email("b@test.com")
                .profile(UserProfile.builder()
                        .fullName("บี ผู้ขอเคลม")
                        .avatarUrl("https://storage.googleapis.com/bucket/uploads/b-avatar.png")
                        .build())
                .build();

        Claim claim = buildClaim(claimant);

        ClaimResponse response = claimMapper.toResponse(claim);

        assertThat(response.getClaimantAvatarUrl())
                .isEqualTo("https://storage.googleapis.com/bucket/uploads/b-avatar.png");
        assertThat(response.getClaimantName()).isEqualTo("บี ผู้ขอเคลม");
    }

    @Test
    void toResponse_shouldReturnNullAvatarUrl_whenClaimantHasNoAvatar() {
        // TC-09: ผู้ขอเคลมยังไม่มีรูปโปรไฟล์ -> claimantAvatarUrl ต้องเป็น null
        // (ไม่ใช่ string ว่างหรือ path พัง) ให้ frontend ไปแสดงวงกลมตัวอักษรย่อแทน
        User claimant = User.builder()
                .id(UUID.randomUUID())
                .email("c@test.com")
                .profile(UserProfile.builder()
                        .fullName("ซี ผู้ขอเคลม")
                        .avatarUrl(null)
                        .build())
                .build();

        Claim claim = buildClaim(claimant);

        ClaimResponse response = claimMapper.toResponse(claim);

        assertThat(response.getClaimantAvatarUrl()).isNull();
        assertThat(response.getClaimantName()).isEqualTo("ซี ผู้ขอเคลม");
    }

    @Test
    void toResponse_shouldFallBackToEmail_whenClaimantHasNoProfileAtAll() {
        // เคส edge เพิ่มเติม: บัญชีที่ยังไม่มี UserProfile เลย (ไม่ใช่แค่ไม่มี avatar)
        User claimant = User.builder()
                .id(UUID.randomUUID())
                .email("noprofile@test.com")
                .profile(null)
                .build();

        Claim claim = buildClaim(claimant);

        ClaimResponse response = claimMapper.toResponse(claim);

        assertThat(response.getClaimantAvatarUrl()).isNull();
        assertThat(response.getClaimantName()).isEqualTo("noprofile@test.com");
    }

    private Claim buildClaim(User claimant) {
        Report report = Report.builder()
                .id(UUID.randomUUID())
                .title("กระเป๋าสีดำหาย")
                .locationName("โรงอาหาร")
                .status(ReportStatus.MATCH_PENDING)
                .images(List.of())
                .build();

        return Claim.builder()
                .id(UUID.randomUUID())
                .report(report)
                .claimant(claimant)
                .evidenceText("มีสติกเกอร์แมวติดอยู่")
                .claimStatus(ClaimStatus.PENDING)
                .build();
    }
}
