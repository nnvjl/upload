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

package com.clinbrain.bd.core.parse.extractor.impl.common.index;

import com.google.common.base.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import com.clinbrain.bd.core.parse.extractor.api.CollectionSQLSegmentExtractor;
import com.clinbrain.bd.core.parse.extractor.impl.common.index.IndexExtractor;
import com.clinbrain.bd.core.parse.extractor.util.ExtractorUtils;
import com.clinbrain.bd.core.parse.extractor.util.RuleName;
import com.clinbrain.bd.core.parse.sql.segment.ddl.index.IndexSegment;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Indexes extractor.
 *
 * @author duhongjun
 */
public final class IndexesExtractor implements CollectionSQLSegmentExtractor {
    
    private final com.clinbrain.bd.core.parse.extractor.impl.common.index.IndexExtractor indexExtractor = new IndexExtractor();
    
    @Override
    public Collection<IndexSegment> extract(final ParserRuleContext ancestorNode, final Map<ParserRuleContext, Integer> parameterMarkerIndexes) {
        Collection<IndexSegment> result = new LinkedList<>();
        for (ParserRuleContext each : ExtractorUtils.getAllDescendantNodes(ancestorNode, RuleName.INDEX_NAME)) {
            Optional<IndexSegment> indexSegment = indexExtractor.extract(each, parameterMarkerIndexes);
            if (indexSegment.isPresent()) {
                result.add(indexSegment.get());
            }
        }
        return result;
    }
}
