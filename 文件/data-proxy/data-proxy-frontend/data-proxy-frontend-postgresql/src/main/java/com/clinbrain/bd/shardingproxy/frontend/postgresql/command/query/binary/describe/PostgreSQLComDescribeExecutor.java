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

package com.clinbrain.bd.shardingproxy.frontend.postgresql.command.query.binary.describe;

import com.clinbrain.bd.shardingproxy.frontend.api.CommandExecutor;
import com.clinbrain.bd.shardingproxy.transport.packet.DatabasePacket;

import java.util.Collection;
import java.util.Collections;

/**
 * Command describe for PostgreSQL.
 *
 * @author zhangyonglun
 * @author zhangliang
 */
public final class PostgreSQLComDescribeExecutor implements CommandExecutor {
    
    @Override
    public Collection<DatabasePacket> execute() {
        return Collections.emptyList();
    }
}
