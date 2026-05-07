package com.example.stat.inquiry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.stat.inquiry.dto.InquiryStatSearchCondition;
import com.example.stat.inquiry.service.InquiryStatService;
import com.example.stat.inquiry.type.InquiryCategory;
import com.example.stat.inquiry.type.InquiryStatus;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InquiryStatController {

	private final InquiryStatService inquiryStatService;

	@GetMapping("/inquiries/stats/v1")
	public String statV1(InquiryStatSearchCondition condition, Model model) {
		model.addAttribute("total_total", inquiryStatService.countStatV1(condition, InquiryCategory.ALL.getCode(), InquiryStatus.TOTAL.getCode()));
		model.addAttribute("total_received", inquiryStatService.countStatV1(condition, InquiryCategory.ALL.getCode(), InquiryStatus.RECEIVED.getCode()));
		model.addAttribute("total_inProgress", inquiryStatService.countStatV1(condition, InquiryCategory.ALL.getCode(), InquiryStatus.IN_PROGRESS.getCode()));
		model.addAttribute("total_resolved", inquiryStatService.countStatV1(condition, InquiryCategory.ALL.getCode(), InquiryStatus.RESOLVED.getCode()));
		model.addAttribute("total_onHold", inquiryStatService.countStatV1(condition, InquiryCategory.ALL.getCode(), InquiryStatus.ON_HOLD.getCode()));
		model.addAttribute("total_reopened", inquiryStatService.countStatV1(condition, InquiryCategory.ALL.getCode(), InquiryStatus.REOPENED.getCode()));
		model.addAttribute("total_transferred", inquiryStatService.countStatV1(condition, InquiryCategory.ALL.getCode(), InquiryStatus.TRANSFERRED.getCode()));
		model.addAttribute("total_unresolved", inquiryStatService.countStatV1(condition, InquiryCategory.ALL.getCode(), InquiryStatus.UNRESOLVED.getCode()));

		model.addAttribute("paymentRefund_total", inquiryStatService.countStatV1(condition, InquiryCategory.PAYMENT_REFUND.getCode(), InquiryStatus.TOTAL.getCode()));
		model.addAttribute("paymentRefund_received", inquiryStatService.countStatV1(condition, InquiryCategory.PAYMENT_REFUND.getCode(), InquiryStatus.RECEIVED.getCode()));
		model.addAttribute("paymentRefund_inProgress", inquiryStatService.countStatV1(condition, InquiryCategory.PAYMENT_REFUND.getCode(), InquiryStatus.IN_PROGRESS.getCode()));
		model.addAttribute("paymentRefund_resolved", inquiryStatService.countStatV1(condition, InquiryCategory.PAYMENT_REFUND.getCode(), InquiryStatus.RESOLVED.getCode()));
		model.addAttribute("paymentRefund_onHold", inquiryStatService.countStatV1(condition, InquiryCategory.PAYMENT_REFUND.getCode(), InquiryStatus.ON_HOLD.getCode()));
		model.addAttribute("paymentRefund_reopened", inquiryStatService.countStatV1(condition, InquiryCategory.PAYMENT_REFUND.getCode(), InquiryStatus.REOPENED.getCode()));
		model.addAttribute("paymentRefund_transferred", inquiryStatService.countStatV1(condition, InquiryCategory.PAYMENT_REFUND.getCode(), InquiryStatus.TRANSFERRED.getCode()));
		model.addAttribute("paymentRefund_unresolved", inquiryStatService.countStatV1(condition, InquiryCategory.PAYMENT_REFUND.getCode(), InquiryStatus.UNRESOLVED.getCode()));

		model.addAttribute("accountLogin_total", inquiryStatService.countStatV1(condition, InquiryCategory.ACCOUNT_LOGIN.getCode(), InquiryStatus.TOTAL.getCode()));
		model.addAttribute("accountLogin_received", inquiryStatService.countStatV1(condition, InquiryCategory.ACCOUNT_LOGIN.getCode(), InquiryStatus.RECEIVED.getCode()));
		model.addAttribute("accountLogin_inProgress", inquiryStatService.countStatV1(condition, InquiryCategory.ACCOUNT_LOGIN.getCode(), InquiryStatus.IN_PROGRESS.getCode()));
		model.addAttribute("accountLogin_resolved", inquiryStatService.countStatV1(condition, InquiryCategory.ACCOUNT_LOGIN.getCode(), InquiryStatus.RESOLVED.getCode()));
		model.addAttribute("accountLogin_onHold", inquiryStatService.countStatV1(condition, InquiryCategory.ACCOUNT_LOGIN.getCode(), InquiryStatus.ON_HOLD.getCode()));
		model.addAttribute("accountLogin_reopened", inquiryStatService.countStatV1(condition, InquiryCategory.ACCOUNT_LOGIN.getCode(), InquiryStatus.REOPENED.getCode()));
		model.addAttribute("accountLogin_transferred", inquiryStatService.countStatV1(condition, InquiryCategory.ACCOUNT_LOGIN.getCode(), InquiryStatus.TRANSFERRED.getCode()));
		model.addAttribute("accountLogin_unresolved", inquiryStatService.countStatV1(condition, InquiryCategory.ACCOUNT_LOGIN.getCode(), InquiryStatus.UNRESOLVED.getCode()));

		model.addAttribute("deliveryBooking_total", inquiryStatService.countStatV1(condition, InquiryCategory.DELIVERY_BOOKING.getCode(), InquiryStatus.TOTAL.getCode()));
		model.addAttribute("deliveryBooking_received", inquiryStatService.countStatV1(condition, InquiryCategory.DELIVERY_BOOKING.getCode(), InquiryStatus.RECEIVED.getCode()));
		model.addAttribute("deliveryBooking_inProgress", inquiryStatService.countStatV1(condition, InquiryCategory.DELIVERY_BOOKING.getCode(), InquiryStatus.IN_PROGRESS.getCode()));
		model.addAttribute("deliveryBooking_resolved", inquiryStatService.countStatV1(condition, InquiryCategory.DELIVERY_BOOKING.getCode(), InquiryStatus.RESOLVED.getCode()));
		model.addAttribute("deliveryBooking_onHold", inquiryStatService.countStatV1(condition, InquiryCategory.DELIVERY_BOOKING.getCode(), InquiryStatus.ON_HOLD.getCode()));
		model.addAttribute("deliveryBooking_reopened", inquiryStatService.countStatV1(condition, InquiryCategory.DELIVERY_BOOKING.getCode(), InquiryStatus.REOPENED.getCode()));
		model.addAttribute("deliveryBooking_transferred", inquiryStatService.countStatV1(condition, InquiryCategory.DELIVERY_BOOKING.getCode(), InquiryStatus.TRANSFERRED.getCode()));
		model.addAttribute("deliveryBooking_unresolved", inquiryStatService.countStatV1(condition, InquiryCategory.DELIVERY_BOOKING.getCode(), InquiryStatus.UNRESOLVED.getCode()));

		model.addAttribute("bugError_total", inquiryStatService.countStatV1(condition, InquiryCategory.BUG_ERROR.getCode(), InquiryStatus.TOTAL.getCode()));
		model.addAttribute("bugError_received", inquiryStatService.countStatV1(condition, InquiryCategory.BUG_ERROR.getCode(), InquiryStatus.RECEIVED.getCode()));
		model.addAttribute("bugError_inProgress", inquiryStatService.countStatV1(condition, InquiryCategory.BUG_ERROR.getCode(), InquiryStatus.IN_PROGRESS.getCode()));
		model.addAttribute("bugError_resolved", inquiryStatService.countStatV1(condition, InquiryCategory.BUG_ERROR.getCode(), InquiryStatus.RESOLVED.getCode()));
		model.addAttribute("bugError_onHold", inquiryStatService.countStatV1(condition, InquiryCategory.BUG_ERROR.getCode(), InquiryStatus.ON_HOLD.getCode()));
		model.addAttribute("bugError_reopened", inquiryStatService.countStatV1(condition, InquiryCategory.BUG_ERROR.getCode(), InquiryStatus.REOPENED.getCode()));
		model.addAttribute("bugError_transferred", inquiryStatService.countStatV1(condition, InquiryCategory.BUG_ERROR.getCode(), InquiryStatus.TRANSFERRED.getCode()));
		model.addAttribute("bugError_unresolved", inquiryStatService.countStatV1(condition, InquiryCategory.BUG_ERROR.getCode(), InquiryStatus.UNRESOLVED.getCode()));

		model.addAttribute("serviceUsage_total", inquiryStatService.countStatV1(condition, InquiryCategory.SERVICE_USAGE.getCode(), InquiryStatus.TOTAL.getCode()));
		model.addAttribute("serviceUsage_received", inquiryStatService.countStatV1(condition, InquiryCategory.SERVICE_USAGE.getCode(), InquiryStatus.RECEIVED.getCode()));
		model.addAttribute("serviceUsage_inProgress", inquiryStatService.countStatV1(condition, InquiryCategory.SERVICE_USAGE.getCode(), InquiryStatus.IN_PROGRESS.getCode()));
		model.addAttribute("serviceUsage_resolved", inquiryStatService.countStatV1(condition, InquiryCategory.SERVICE_USAGE.getCode(), InquiryStatus.RESOLVED.getCode()));
		model.addAttribute("serviceUsage_onHold", inquiryStatService.countStatV1(condition, InquiryCategory.SERVICE_USAGE.getCode(), InquiryStatus.ON_HOLD.getCode()));
		model.addAttribute("serviceUsage_reopened", inquiryStatService.countStatV1(condition, InquiryCategory.SERVICE_USAGE.getCode(), InquiryStatus.REOPENED.getCode()));
		model.addAttribute("serviceUsage_transferred", inquiryStatService.countStatV1(condition, InquiryCategory.SERVICE_USAGE.getCode(), InquiryStatus.TRANSFERRED.getCode()));
		model.addAttribute("serviceUsage_unresolved", inquiryStatService.countStatV1(condition, InquiryCategory.SERVICE_USAGE.getCode(), InquiryStatus.UNRESOLVED.getCode()));

		model.addAttribute("partnership_total", inquiryStatService.countStatV1(condition, InquiryCategory.PARTNERSHIP.getCode(), InquiryStatus.TOTAL.getCode()));
		model.addAttribute("partnership_received", inquiryStatService.countStatV1(condition, InquiryCategory.PARTNERSHIP.getCode(), InquiryStatus.RECEIVED.getCode()));
		model.addAttribute("partnership_inProgress", inquiryStatService.countStatV1(condition, InquiryCategory.PARTNERSHIP.getCode(), InquiryStatus.IN_PROGRESS.getCode()));
		model.addAttribute("partnership_resolved", inquiryStatService.countStatV1(condition, InquiryCategory.PARTNERSHIP.getCode(), InquiryStatus.RESOLVED.getCode()));
		model.addAttribute("partnership_onHold", inquiryStatService.countStatV1(condition, InquiryCategory.PARTNERSHIP.getCode(), InquiryStatus.ON_HOLD.getCode()));
		model.addAttribute("partnership_reopened", inquiryStatService.countStatV1(condition, InquiryCategory.PARTNERSHIP.getCode(), InquiryStatus.REOPENED.getCode()));
		model.addAttribute("partnership_transferred", inquiryStatService.countStatV1(condition, InquiryCategory.PARTNERSHIP.getCode(), InquiryStatus.TRANSFERRED.getCode()));
		model.addAttribute("partnership_unresolved", inquiryStatService.countStatV1(condition, InquiryCategory.PARTNERSHIP.getCode(), InquiryStatus.UNRESOLVED.getCode()));

		model.addAttribute("etc_total", inquiryStatService.countStatV1(condition, InquiryCategory.ETC.getCode(), InquiryStatus.TOTAL.getCode()));
		model.addAttribute("etc_received", inquiryStatService.countStatV1(condition, InquiryCategory.ETC.getCode(), InquiryStatus.RECEIVED.getCode()));
		model.addAttribute("etc_inProgress", inquiryStatService.countStatV1(condition, InquiryCategory.ETC.getCode(), InquiryStatus.IN_PROGRESS.getCode()));
		model.addAttribute("etc_resolved", inquiryStatService.countStatV1(condition, InquiryCategory.ETC.getCode(), InquiryStatus.RESOLVED.getCode()));
		model.addAttribute("etc_onHold", inquiryStatService.countStatV1(condition, InquiryCategory.ETC.getCode(), InquiryStatus.ON_HOLD.getCode()));
		model.addAttribute("etc_reopened", inquiryStatService.countStatV1(condition, InquiryCategory.ETC.getCode(), InquiryStatus.REOPENED.getCode()));
		model.addAttribute("etc_transferred", inquiryStatService.countStatV1(condition, InquiryCategory.ETC.getCode(), InquiryStatus.TRANSFERRED.getCode()));
		model.addAttribute("etc_unresolved", inquiryStatService.countStatV1(condition, InquiryCategory.ETC.getCode(), InquiryStatus.UNRESOLVED.getCode()));

		return "inquiry/stat-v1";
	}

	@GetMapping("/inquiries/stats/v2")
	public String statV2(InquiryStatSearchCondition condition, Model model) {
		model.addAttribute("statTable", inquiryStatService.getStatTableV2(condition));
		return "inquiry/stat-v2";
	}

	@GetMapping("/inquiries/stats/v3")
	public String statV3(InquiryStatSearchCondition condition, Model model) {
		model.addAttribute("statTable", inquiryStatService.getStatTableV3(condition));
		return "inquiry/stat-v3";
	}
}
