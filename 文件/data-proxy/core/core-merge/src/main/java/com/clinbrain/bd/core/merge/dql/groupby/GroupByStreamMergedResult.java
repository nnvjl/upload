/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clinbrain.bd.core.merge.dql.groupby;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.clinbrain.bd.core.execute.sql.execute.result.QueryResult;
import com.clinbrain.bd.core.merge.dql.groupby.aggregation.AggregationUnit;
import com.clinbrain.bd.core.merge.dql.groupby.aggregation.AggregationUnitFactory;
import com.clinbrain.bd.core.merge.dql.orderby.OrderByStreamMergedResult;
import com.clinbrain.bd.core.parse.sql.context.selectitem.AggregationSelectItem;
import com.clinbrain.bd.core.parse.sql.statement.dml.SelectStatement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Stream merged result for group by.
 *
 * @author zhangliang
 */
public final class GroupByStreamMergedResult extends OrderByStreamMergedResult {
    
    private final Map<String, Integer> labelAndIndexMap;
    
    private final SelectStatement selectStatement;
    
    private final List<Object> currentRow;
    
    private List<?> currentGroupByValues;
    
    public GroupByStreamMergedResult(
            final Map<String, Integer> labelAndIndexMap, final List<QueryResult> queryResults, final SelectStatement selectStatement) throws SQLException {
        super(queryResults, selectStatement.getOrderByItems());
        this.labelAndIndexMap = labelAndIndexMap;
        this.selectStatement = selectStatement;
        currentRow = new ArrayList<>(labelAndIndexMap.size());
        currentGroupByValues = getOrderByValuesQueue().isEmpty() ? Collections.emptyList() : new GroupByValue(getCurrentQueryResult(), selectStatement.getGroupByItems()).getGroupValues();
    }
    
    @Override
    public boolean next() throws SQLException {
        currentRow.clear();
        if (getOrderByValuesQueue().isEmpty()) {
            return false;
        }
        if (isFirstNext()) {
            super.next();
        }
        if (aggregateCurrentGroupByRowAndNext()) {
            currentGroupByValues = new GroupByValue(getCurrentQueryResult(), selectStatement.getGroupByItems()).getGroupValues();
        }
        return true;
    }
    
    private boolean aggregateCurrentGroupByRowAndNext() throws SQLException {
        boolean result = false;
        Map<AggregationSelectItem, AggregationUnit> aggregationUnitMap = Maps.toMap(selectStatement.getAggregationSelectItems(), new Function<AggregationSelectItem, AggregationUnit>() {
            
            @Override
            public AggregationUnit apply(final AggregationSelectItem input) {
                return AggregationUnitFactory.create(input.getType());
            }
        });
        while (currentGroupByValues.equals(new GroupByValue(getCurrentQueryResult(), selectStatement.getGroupByItems()).getGroupValues())) {
            aggregate(aggregationUnitMap);
            cacheCurrentRow();
            result = super.next();
            if (!result) {
                break;
            }
        }
        setAggregationValueToCurrentRow(aggregationUnitMap);
        return result;
    }
    
    private void aggregate(final Map<AggregationSelectItem, AggregationUnit> aggregationUnitMap) throws SQLException {
        for (Entry<AggregationSelectItem, AggregationUnit> entry : aggregationUnitMap.entrySet()) {
            List<Comparable<?>> values = new ArrayList<>(2);
            if (entry.getKey().getDerivedAggregationSelectItems().isEmpty()) {
                values.add(getAggregationValue(entry.getKey()));
            } else {
                for (AggregationSelectItem each : entry.getKey().getDerivedAggregationSelectItems()) {
                    values.add(getAggregationValue(each));
                }
            }
            entry.getValue().merge(values);
        }
    }
    
    private void cacheCurrentRow() throws SQLException {
        for (int i = 0; i < getCurrentQueryResult().getColumnCount(); i++) {
            currentRow.add(getCurrentQueryResult().getValue(i + 1, Object.class));
        }
    }
    
    private Comparable<?> getAggregationValue(final AggregationSelectItem aggregationSelectItem) throws SQLException {
        Object result = getCurrentQueryResult().getValue(aggregationSelectItem.getIndex(), Object.class);
        Preconditions.checkState(null == result || result instanceof Comparable, "Aggregation value must implements Comparable");
        return (Comparable<?>) result;
    }
    
    private void setAggregationValueToCurrentRow(final Map<AggregationSelectItem, AggregationUnit> aggregationUnitMap) {
        for (Entry<AggregationSelectItem, AggregationUnit> entry : aggregationUnitMap.entrySet()) {
            currentRow.set(entry.getKey().getIndex() - 1, entry.getValue().getResult());
        }
    }
    
    @Override
    public Object getValue(final int columnIndex, final Class<?> type) {
        return currentRow.get(columnIndex - 1);
    }
    
    @Override
    public Object getValue(final String columnLabel, final Class<?> type) {
        Preconditions.checkState(labelAndIndexMap.containsKey(columnLabel), "Can't find columnLabel: %s", columnLabel);
        return currentRow.get(labelAndIndexMap.get(columnLabel) - 1);
    }
    
    @Override
    public Object getCalendarValue(final int columnIndex, final Class<?> type, final Calendar calendar) {
        return currentRow.get(columnIndex - 1);
    }
    
    @Override
    public Object getCalendarValue(final String columnLabel, final Class<?> type, final Calendar calendar) {
        Preconditions.checkState(labelAndIndexMap.containsKey(columnLabel), "Can't find columnLabel: %s", columnLabel);
        return currentRow.get(labelAndIndexMap.get(columnLabel) - 1);
    }
}
