package com.example.lostandfound.service.strategy;

import com.example.lostandfound.domain.entity.Report;
import com.example.lostandfound.domain.entity.User;
import com.example.lostandfound.domain.enums.ReportType;
import com.example.lostandfound.dto.request.SubmitClaimRequest;
import com.example.lostandfound.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClaimEligibilityStrategyTest {

    private final Report dummyReport = Report.builder().build();
    private final User dummyUser = User.builder().build();

    @Test
    void lostStrategy_shouldReject_whenEvidenceTextTooShort() {
        SubmitClaimRequest request = new SubmitClaimRequest();
        request.setEvidenceText("สั้น");

        assertThatThrownBy(() -> new LostReportClaimEligibilityStrategy()
                .validate(dummyReport, dummyUser, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void lostStrategy_shouldPass_whenEvidenceTextLongEnough() {
        SubmitClaimRequest request = new SubmitClaimRequest();
        request.setEvidenceText("เจอกระเป๋าสะพายสีดำที่โรงอาหารตึกวิทย์ ชั้น 1");

        assertThatCode(() -> new LostReportClaimEligibilityStrategy()
                .validate(dummyReport, dummyUser, request))
                .doesNotThrowAnyException();
    }

    @Test
    void lostStrategy_supports_shouldReturnLostType() {
        assertThat(new LostReportClaimEligibilityStrategy().supports()).isEqualTo(ReportType.LOST);
    }

    @Test
    void foundStrategy_shouldReject_whenNoEvidenceAtAll() {
        SubmitClaimRequest request = new SubmitClaimRequest();
        request.setEvidenceText("");

        assertThatThrownBy(() -> new FoundReportClaimEligibilityStrategy()
                .validate(dummyReport, dummyUser, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void foundStrategy_shouldPass_whenImageProvided() {
        SubmitClaimRequest request = new SubmitClaimRequest();
        request.setEvidenceImageUrl("https://example.com/proof.jpg");

        assertThatCode(() -> new FoundReportClaimEligibilityStrategy()
                .validate(dummyReport, dummyUser, request))
                .doesNotThrowAnyException();
    }

    @Test
    void foundStrategy_supports_shouldReturnFoundType() {
        assertThat(new FoundReportClaimEligibilityStrategy().supports()).isEqualTo(ReportType.FOUND);
    }

    @Test
    void resolver_shouldReturnCorrectStrategyByType() {
        ClaimEligibilityStrategyResolver resolver = new ClaimEligibilityStrategyResolver(
                List.of(new LostReportClaimEligibilityStrategy(), new FoundReportClaimEligibilityStrategy()));

        assertThat(resolver.resolve(ReportType.LOST)).isInstanceOf(LostReportClaimEligibilityStrategy.class);
        assertThat(resolver.resolve(ReportType.FOUND)).isInstanceOf(FoundReportClaimEligibilityStrategy.class);
    }

    @Test
    void resolver_shouldReturnNull_whenNoStrategyMatchesType() {
        ClaimEligibilityStrategyResolver resolver = new ClaimEligibilityStrategyResolver(
                List.of(new LostReportClaimEligibilityStrategy()));

        assertThat(resolver.resolve(ReportType.FOUND)).isNull();
    }
}