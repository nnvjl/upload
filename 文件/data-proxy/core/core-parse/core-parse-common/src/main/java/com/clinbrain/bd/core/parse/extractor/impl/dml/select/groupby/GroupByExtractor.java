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

package com.clinbrain.bd.core.parse.extractor.impl.dml.select.groupby;

import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.ParserRuleContext;
import com.clinbrain.bd.core.parse.extractor.api.OptionalSQLSegmentExtractor;
import com.clinbrain.bd.core.parse.extractor.impl.dml.select.orderby.OrderByItemExtractor;
import com.clinbrain.bd.core.parse.extractor.util.ExtractorUtils;
import com.clinbrain.bd.core.parse.extractor.util.RuleName;
import com.clinbrain.bd.core.parse.sql.segment.dml.order.GroupBySegment;

import java.util.Map;

/**
 * Group by extractor.
 *
 * @author duhongjun
 * @author panjuan
 * @author zhangliang
 */
@RequiredArgsConstructor
public abstract class GroupByExtractor implements OptionalSQLSegmentExtractor {
    
    private final OrderByItemExtractor orderByItemExtractor;
    
    @Override
    public final Optional<GroupBySegment> extract(final ParserRuleContext ancestorNode, final Map<ParserRuleContext, Integer> parameterMarkerIndexes) {
        Optional<ParserRuleContext> groupByNode = ExtractorUtils.findFirstChildNode(findMainQueryNode(ancestorNode), RuleName.GROUP_BY_CLAUSE);
        return groupByNode.isPresent() ? Optional.of(
                new GroupBySegment(groupByNode.get().getStart().getStartIndex(), groupByNode.get().getStop().getStopIndex(), orderByItemExtractor.extract(groupByNode.get(), parameterMarkerIndexes)))
                : Optional.<GroupBySegment>absent();
    }
    
    private ParserRuleContext findMainQueryNode(final ParserRuleContext ancestorNode) {
        Optional<ParserRuleContext> tableReferencesNode = ExtractorUtils.findFirstChildNode(ancestorNode, RuleName.TABLE_REFERENCES);
        if (!tableReferencesNode.isPresent()) {
            return ancestorNode;
        }
        Optional<ParserRuleContext> subqueryNode = ExtractorUtils.findSingleNodeFromFirstDescendant(tableReferencesNode.get(), RuleName.SUBQUERY);
        if (subqueryNode.isPresent()) {
            return findMainQueryNode(subqueryNode.get());
        }
        return ancestorNode;
    }
}
