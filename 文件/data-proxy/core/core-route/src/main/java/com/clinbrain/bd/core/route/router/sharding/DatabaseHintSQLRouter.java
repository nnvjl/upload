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

package com.clinbrain.bd.core.route.router.sharding;

import lombok.RequiredArgsConstructor;
import com.clinbrain.bd.core.parse.SQLParseEngine;
import com.clinbrain.bd.core.parse.rule.registry.MasterSlaveParseRuleRegistry;
import com.clinbrain.bd.core.parse.sql.statement.SQLStatement;
import com.clinbrain.bd.core.route.SQLRouteResult;
import com.clinbrain.bd.core.route.type.RoutingResult;
import com.clinbrain.bd.core.route.type.hint.DatabaseHintRoutingEngine;
import com.clinbrain.bd.core.rule.ShardingRule;
import com.clinbrain.bd.core.strategy.route.hint.HintShardingStrategy;
import com.clinbrain.bd.spi.DbType;

import java.util.List;

/**
 * Sharding router for hint database only.
 * 
 * @author zhangliang
 * @author maxiaoguang
 */
// TODO removed after all ANTLR parser finished
@RequiredArgsConstructor
public final class DatabaseHintSQLRouter implements ShardingRouter {
    
    private final DbType databaseType;
    
    private final ShardingRule shardingRule;
    
    @Override
    public SQLStatement parse(final String logicSQL, final boolean useCache) {
        return new SQLParseEngine(MasterSlaveParseRuleRegistry.getInstance(), databaseType, logicSQL, null, null).parse();
    }
    
    @Override
    // TODO insert SQL need parse gen key
    public SQLRouteResult route(final SQLStatement sqlStatement, final List<Object> parameters) {
        SQLRouteResult result = new SQLRouteResult(sqlStatement);
        RoutingResult routingResult = new DatabaseHintRoutingEngine(
                shardingRule.getShardingDataSourceNames().getDataSourceNames(), (HintShardingStrategy) shardingRule.getDefaultDatabaseShardingStrategy()).route();
        result.setRoutingResult(routingResult);
        return result;
    }
}
