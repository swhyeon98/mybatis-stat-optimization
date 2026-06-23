package com.example.stat.inquiry.service;

import com.example.stat.inquiry.dto.*;
import com.example.stat.inquiry.mapper.InquiryStatMapper;
import com.example.stat.inquiry.type.InquiryCategory;
import com.example.stat.inquiry.type.InquiryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryStatService {

    private final InquiryStatMapper inquiryStatMapper;

    public int countStatV1(InquiryStatSearchCondition condition, String category, String status) {
        return inquiryStatMapper.countStatV1(condition, category, status);
    }

    public InquiryStatTable getStatTableV2(InquiryStatSearchCondition condition) {
        List<InquiryStatRow> rows = new ArrayList<>();
        for (InquiryCategory category : statCategories()) {
            InquiryStatAggregateRow aggregateRow = inquiryStatMapper.selectStatRowV2(condition, category.getCode());
            if (aggregateRow == null) {
                aggregateRow = emptyAggregateRow(category.getCode());
            }
            aggregateRow.setCategoryCode(category.getCode());
            rows.add(toStatRow(category.getLabel(), category.getCode(), aggregateRow));
        }

        return new InquiryStatTable(statColumns(), rows);
    }

    public InquiryStatTable getStatTableV3(InquiryStatSearchCondition condition) {
        Map<String, InquiryStatAggregateRow> aggregateRowMap = inquiryStatMapper.selectStatRowsV3(condition).stream()
                .collect(Collectors.toMap(InquiryStatAggregateRow::getCategoryCode, Function.identity()));

        InquiryStatAggregateRow totalAggregateRow = new InquiryStatAggregateRow();
        List<InquiryStatRow> categoryRows = new ArrayList<>();
        for (InquiryCategory category : storedCategories()) {
            InquiryStatAggregateRow aggregateRow = aggregateRowMap.getOrDefault(
                    category.getCode(),
                    emptyAggregateRow(category.getCode())
            );
            addToTotal(totalAggregateRow, aggregateRow);
            categoryRows.add(toStatRow(category.getLabel(), category.getCode(), aggregateRow));
        }

        List<InquiryStatRow> rows = new ArrayList<>();
        rows.add(toStatRow(InquiryCategory.ALL.getLabel(), InquiryCategory.ALL.getCode(), totalAggregateRow));
        rows.addAll(categoryRows);

        return new InquiryStatTable(statColumns(), rows);
    }

    private List<InquiryStatColumn> statColumns() {
        return List.of(
                new InquiryStatColumn("total", InquiryStatus.TOTAL.getLabel(), InquiryStatus.TOTAL.getCode()),
                new InquiryStatColumn("received", InquiryStatus.RECEIVED.getLabel(), InquiryStatus.RECEIVED.getCode()),
                new InquiryStatColumn("inProgress", InquiryStatus.IN_PROGRESS.getLabel(), InquiryStatus.IN_PROGRESS.getCode()),
                new InquiryStatColumn("resolved", InquiryStatus.RESOLVED.getLabel(), InquiryStatus.RESOLVED.getCode()),
                new InquiryStatColumn("onHold", InquiryStatus.ON_HOLD.getLabel(), InquiryStatus.ON_HOLD.getCode()),
                new InquiryStatColumn("reopened", InquiryStatus.REOPENED.getLabel(), InquiryStatus.REOPENED.getCode()),
                new InquiryStatColumn("transferred", InquiryStatus.TRANSFERRED.getLabel(), InquiryStatus.TRANSFERRED.getCode()),
                new InquiryStatColumn("unresolved", InquiryStatus.UNRESOLVED.getLabel(), InquiryStatus.UNRESOLVED.getCode())
        );
    }

    private List<InquiryCategory> statCategories() {
        List<InquiryCategory> categories = new ArrayList<>();
        categories.add(InquiryCategory.ALL);
        categories.addAll(storedCategories());
        return categories;
    }

    private List<InquiryCategory> storedCategories() {
        return List.of(
                InquiryCategory.PAYMENT_REFUND,
                InquiryCategory.ACCOUNT_LOGIN,
                InquiryCategory.DELIVERY_BOOKING,
                InquiryCategory.BUG_ERROR,
                InquiryCategory.SERVICE_USAGE,
                InquiryCategory.PARTNERSHIP,
                InquiryCategory.ETC
        );
    }

    private InquiryStatAggregateRow emptyAggregateRow(String categoryCode) {
        InquiryStatAggregateRow aggregateRow = new InquiryStatAggregateRow();
        aggregateRow.setCategoryCode(categoryCode);
        return aggregateRow;
    }

    private void addToTotal(InquiryStatAggregateRow total, InquiryStatAggregateRow row) {
        total.setTotalCount(total.getTotalCount() + row.getTotalCount());
        total.setReceivedCount(total.getReceivedCount() + row.getReceivedCount());
        total.setInProgressCount(total.getInProgressCount() + row.getInProgressCount());
        total.setResolvedCount(total.getResolvedCount() + row.getResolvedCount());
        total.setOnHoldCount(total.getOnHoldCount() + row.getOnHoldCount());
        total.setReopenedCount(total.getReopenedCount() + row.getReopenedCount());
        total.setTransferredCount(total.getTransferredCount() + row.getTransferredCount());
        total.setUnresolvedCount(total.getUnresolvedCount() + row.getUnresolvedCount());
    }

    private InquiryStatRow toStatRow(String label, String categoryCode, InquiryStatAggregateRow aggregateRow) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("total", aggregateRow.getTotalCount());
        counts.put("received", aggregateRow.getReceivedCount());
        counts.put("inProgress", aggregateRow.getInProgressCount());
        counts.put("resolved", aggregateRow.getResolvedCount());
        counts.put("onHold", aggregateRow.getOnHoldCount());
        counts.put("reopened", aggregateRow.getReopenedCount());
        counts.put("transferred", aggregateRow.getTransferredCount());
        counts.put("unresolved", aggregateRow.getUnresolvedCount());
        return new InquiryStatRow(label, categoryCode, counts);
    }
}
