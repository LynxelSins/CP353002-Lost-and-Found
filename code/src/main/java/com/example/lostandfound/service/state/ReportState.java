package com.example.lostandfound.service.state;

import com.example.lostandfound.domain.enums.ReportStatus;

/**
 * GoF State Pattern: แต่ละสถานะของ Report รู้ตัวเองว่า "เปลี่ยนไปสถานะไหนต่อได้บ้าง"
 * แทนที่จะเขียน if-else เช็คสถานะกระจายอยู่หลายที่ใน ReportStatusChanger
 */
public interface ReportState {

    ReportStatus getStatus();

    boolean canTransitionTo(ReportStatus target);
}